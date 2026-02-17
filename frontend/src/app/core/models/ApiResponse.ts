export interface ApiResponse<T> {
  success: boolean;
  data: T | null;            
  message: string;
  statusCode: number;
  errors?: Record<string, string>;
}
