import { Component } from '@angular/core';
import { products } from '../../core/models/Product';
import { ProductItem } from '../../sub-components/product/product';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ProductItem, CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  public products = products;
}
