import http from 'k6/http';
import { check, sleep } from 'k6';

http.setResponseCallback(http.expectedStatuses(200, { min: 400, max: 499 }));

export const options = {
  stages: [
    { duration: '30s', target: 50 },
    { duration: '1m',  target: 100 },
    { duration: '1m', target: 200  },
  ],
  insecureSkipTLSVerify: true,
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.05'],
  },
};

const BASE_URL = 'https://localhost:10000';

function parseJsonSafe(response) {
  if (!response || !response.body) {
    return null;
  }
  try {
    return JSON.parse(response.body);
  } catch (e) {
    return null;
  }
}

export function setup() {
  const email = `loaduser_${Date.now()}_${Math.floor(Math.random() * 100000)}@test.com`;
  const password = 'password123';

  const registerRes = http.post(
    `${BASE_URL}/api/users/register`,
    JSON.stringify({
      email,
      name: 'Load User',
      password,
      role: 'SELLER',
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );

  check(registerRes, {
    'setup register returns 200': (r) => r.status === 200,
  });

  if (registerRes.status !== 200) {
    throw new Error(`Setup failed: unable to register load user (status ${registerRes.status})`);
  }

  return { email, password };
}

export default function (data) {
  const loginSuccessRes = http.post(
    `${BASE_URL}/api/users/login`,
    JSON.stringify({
      identification: data.email,
      password: data.password,
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );

  const successJson = parseJsonSafe(loginSuccessRes);

  check(loginSuccessRes, {
    'login success responds under 2s': (r) => r.timings.duration < 2000,
    'login success returns 200': (r) => r.status === 200,
    'login success returns token': () => !!successJson && !!successJson.token,
  });

  const token = successJson && successJson.token ? successJson.token : null;

  const productsRes = http.get(
    `${BASE_URL}/api/products/`,
    {
      headers: {
        Authorization: `Bearer ${token || ''}`,
      },
    }
  );

  check(productsRes, {
    'products responds under 2s': (r) => r.timings.duration < 2000,
    'products returns 200': (r) => r.status === 200,
  });

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
    'login fail responds under 2s': (r) => r.timings.duration < 2000,
    'login fail returns 4xx': (r) => r.status >= 400,
  });

  sleep(1);
}
