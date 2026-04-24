import http from 'k6/http';
import { check, sleep } from 'k6';

http.setResponseCallback(http.expectedStatuses(401));

export const options = {
  stages: [
    { duration: '30s', target: 20 },  // ramp up to 20 users
    { duration: '1m',  target: 20 },  // stay at 20 users
    { duration: '10s', target: 0  },  // ramp down
  ],
  insecureSkipTLSVerify: true,
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.05'],
  },
};

const BASE_URL = 'https://localhost:10000';

export default function () {
  const loginRes = http.post(
    `${BASE_URL}/api/users/login`,
    JSON.stringify({
      identification: 'ghost@nowhere.com',
      password: 'wrongpassword',
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );

  check(loginRes, {
    'login responds under 2s': (r) => r.timings.duration < 2000,
    'login returns 4xx': (r) => r.status >= 400,
  });

  sleep(1);
}
