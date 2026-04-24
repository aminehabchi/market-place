import http from 'k6/http';
import { check, sleep } from 'k6';

http.setResponseCallback(http.expectedStatuses(200, 401));

export const options = {
  vus: 1,
  duration: '30s',
  insecureSkipTLSVerify: true,
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.01'],
  },
};

const BASE_URL = 'https://localhost:10000';

function hasMsgField(response) {
  if (!response || !response.body) {
    return false;
  }

  try {
    const data = JSON.parse(response.body);
    return data && data.msg !== undefined;
  } catch (e) {
    return false;
  }
}

export default function () {
  // Test 1 - register
  const registerRes = http.post(
    `${BASE_URL}/api/users/register`,
    JSON.stringify({
      email: `smokeuser_${Date.now()}@test.com`,
      name: 'Smoke User',
      password: 'password123',
      role: 'SELLER',
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );

  check(registerRes, {
    'register status is 200': (r) => r.status === 200,
    'register has msg field': (r) => hasMsgField(r),
  });

  sleep(1);

  // Test 2 - login with wrong password returns 401
  const loginFailRes = http.post(
    `${BASE_URL}/api/users/login`,
    JSON.stringify({
      identification: 'ghost@nowhere.com',
      password: 'wrongpassword',
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );

  check(loginFailRes, {
    'login wrong credentials returns 401': (r) => r.status === 401,
  });

  sleep(1);
}
