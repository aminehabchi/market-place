import { Component, signal } from '@angular/core';
import { Product } from '../../core/models/Product';
import { ProductItem } from '../../sub-components/product/product';
import { CommonModule } from '@angular/common';
import { ProductsService } from '../../core/services/products-service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ProductItem, CommonModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
})
export class Home {
  public products = signal<Product[]>([]);

  constructor(private productsService: ProductsService) {}

  ngOnInit() {
    this.productsService.getAllProducts().subscribe({
      next: (res: any) => {
        this.products.set(res.data);
      },
      error: (err) => {
        console.error('Error loading products', err);
      }
    });
  }
}
