import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-create-product-pop-pup',
  imports: [],
  templateUrl: './create-product-pop-pup.html',
  styleUrl: './create-product-pop-pup.css',
})
export class CreateProductPopPup {
  @Output() closePopUp = new EventEmitter<any>();

  close() {
    this.closePopUp.emit();
  }
}
