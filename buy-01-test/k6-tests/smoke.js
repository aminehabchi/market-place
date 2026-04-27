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

function hasMsgField(response) {
  if (!response || !response.body) {
    return false;
  }

  const data = parseJsonSafe(response);
  return !!data && data.msg !== undefined;
}

export default function () {
  const email = `smokeuser_${Date.now()}@test.com`;
  const password = 'password123';

  // Test 1 - register
  const registerRes = http.post(
    `${BASE_URL}/api/users/register`,
    JSON.stringify({
      email,
      name: 'Smoke User',
      password,
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

  // Test 2 - login with created user returns 200 and token
  const loginSuccessRes = http.post(
    `${BASE_URL}/api/users/login`,
    JSON.stringify({
      identification: email,
      password,
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );

  const loginJson = parseJsonSafe(loginSuccessRes);
  const token = loginJson && loginJson.token ? loginJson.token : null;

  check(loginSuccessRes, {
    'login success returns 200': (r) => r.status === 200,
    'login success has token': () => !!token,
  });

  sleep(1);

  // Test 3 - products endpoint is reachable with auth
  const productsRes = http.get(
    `${BASE_URL}/api/products/`,
    {
      headers: {
        Authorization: `Bearer ${token || ''}`,
      },
    }
  );

  const productsJson = parseJsonSafe(productsRes);
  check(productsRes, {
    'products returns 200': (r) => r.status === 200,
    'products has success wrapper': () => !!productsJson && productsJson.success !== undefined,
  });

  sleep(1);

  // Test 4 - media upload endpoint accepts image payload with auth
  const mediaUploadRes = http.post(
    `${BASE_URL}/api/media/products/`,
    'fake-image-content',
    {
      headers: {
        'Content-Type': 'image/png',
        Authorization: `Bearer ${token || ''}`,
      },
    }
  );

  check(mediaUploadRes, {
    'media upload returns 200': (r) => r.status === 200,
    'media upload returns body': (r) => !!r.body,
  });

  sleep(1);

  // Test 5 - login with wrong password returns 401
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
