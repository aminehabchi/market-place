import { Role } from "./Role";

export interface User {
  id?: string;         
  username: string;
  avatar?: string;
  role: Role;
  createdAt?: string; 
  updatedAt?: string;
}