// In-memory mock to simulate CRUD. Replace with real API calls when backend is ready.
export type UploadMedia = {
  id: string;
  username: string;
  email: string;
  enabled: boolean;
};

const STORAGE_KEY = 'mock_users';

function getStore(): User[] {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (raw) return JSON.parse(raw) as User[];
  const seed: User[] = [
    { id: '1', username: 'alice', email: 'alice@example.com', enabled: true },
    { id: '2', username: 'bob', email: 'bob@example.com', enabled: false },
    { id: '3', username: 'hera', email: 'hera@example.com', enabled: true },
  ];
  localStorage.setItem(STORAGE_KEY, JSON.stringify(seed));
  return seed;
}

function setStore(users: User[]) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(users));
}

export async function listUsers(): Promise<User[]> {
  return Promise.resolve(getStore());
  // For real API:
  // const res = await api.get<User[]>('/users');
  // return res.data;
}

export async function getUser(id: string): Promise<User | undefined> {
  const users = getStore();
  return Promise.resolve(users.find((u) => u.id === id));
}

export async function createUser(u: Omit<User, 'id'>): Promise<User> {
  const users = getStore();
  const id = String(Math.max(0, ...users.map((x) => Number(x.id))) + 1);
  const user = { ...u, id };
  users.push(user);
  setStore(users);
  return Promise.resolve(user);
}

export async function updateUser(id: string, patch: Partial<Omit<User, 'id'>>): Promise<User> {
  const users = getStore();
  const idx = users.findIndex((u) => u.id === id);
  if (idx === -1) throw new Error('Not found');
  const updated = { ...users[idx], ...patch };
  users[idx] = updated;
  setStore(users);
  return Promise.resolve(updated);
}

export async function deleteUser(id: string): Promise<void> {
  const users = getStore();
  const next = users.filter((u) => u.id !== id);
  setStore(next);
  return Promise.resolve();
}
