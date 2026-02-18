import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductItem } from '../../sub-components/product/product';
import { CreateProductPopPup } from '../../sub-components/create-product-pop-pup/create-product-pop-pup';
import { Product, products } from '../../core/models/Product';
import { ProductsService } from '../../core/services/products-service';
@Component({
  selector: 'app-seller-dashboard',
  imports: [ProductItem, CreateProductPopPup, CommonModule],
  templateUrl: './seller-dashboard.html',
  styleUrl: './seller-dashboard.css',
})
export class SellerDashboard {
  public isPopPupOpen: boolean = false;

  public products = signal<Product[]>([]);

  constructor(private productsService: ProductsService) { }

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

  onProductCreated(product: Product) {
    this.products.update(current => [product, ...current]);
    this.isPopPupOpen = false;
  }

  deleteProductById(id: string) {
    this.products.set(
      this.products().filter(product => product.id !== id)
    );
  }


  public togglePopUp() {
    this.isPopPupOpen = !this.isPopPupOpen;
  }
}
