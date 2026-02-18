import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiResponse } from '../models/ApiResponse';
import { Product } from '../models/Product';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductsService {
  private apiUrl = '/api/products';

  constructor(private http: HttpClient) { }

  createProduct(post: Partial<Product>): Observable<ApiResponse<Product>> {
    return this.http.post<ApiResponse<Product>>(
      `${this.apiUrl}/`,
      post,
      {
        headers: { 'Content-Type': 'application/json' }
      }
    );
  }

  getAllProducts(): Observable<ApiResponse<Product[]>> {
    return this.http.get<ApiResponse<Product[]>>(
      `${this.apiUrl}/`
    );
  }

  getMyProducts() {
    return this.http.get<ApiResponse<Product[]>>(
      `${this.apiUrl}/me`
    );
  }
}
