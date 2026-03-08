// In-memory mock to simulate CRUD. Replace with real API calls when backend is ready.
export type UploadMedia = {
  filename: string;
  filepath: string;
  category: string;
};

export async function uploadMedia(u: Omit<UploadMedia, 'id'>): Promise<string> {
  
  return Promise.resolve(user);
}
