import { Component } from '@angular/core';
import { products } from '../../core/models/Product';
import { CommonModule } from '@angular/common';
import { ProductItem } from '../../sub-components/product/product';
import { CreateProductPopPup } from '../../sub-components/create-product-pop-pup/create-product-pop-pup';
@Component({
  selector: 'app-seller-dashboard',
  imports: [ProductItem, CreateProductPopPup, CommonModule],
  templateUrl: './seller-dashboard.html',
  styleUrl: './seller-dashboard.css',
})
export class SellerDashboard {
  public products = products;
  public isPopPupOpen: boolean = false;

  public togglePopUp() {
    this.isPopPupOpen = !this.isPopPupOpen;
  }
}
