import { Component, Input } from '@angular/core';
import { Product } from '../../core/models/Product';

@Component({
  selector: 'app-product',
    standalone: true,
  imports: [],
  templateUrl: './product.html',
  styleUrl: './product.css',
})
export class ProductItem {
  @Input() product!: Product;
}
