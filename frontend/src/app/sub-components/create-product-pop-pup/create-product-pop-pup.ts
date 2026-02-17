import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Product } from '../../core/models/Product';
import { MediaSevice } from '../../core/services/media-sevice';

@Component({
  selector: 'app-create-product-pop-pup',
  imports: [FormsModule, CommonModule],
  templateUrl: './create-product-pop-pup.html',
  styleUrl: './create-product-pop-pup.css',
})
export class CreateProductPopPup {

  constructor(private mediaSevice: MediaSevice) { }

  @Output() closePopUp = new EventEmitter<any>();

  product: Partial<Product> = {
    name: '',
    description: '',
    price: 0,
    image: '',
    userId: 'current-user-id',
  };

  selectedImage = signal<File | null>(null);
  imagePreview = signal<string | null>(null);

  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    if (!this.mediaSevice.isImage(file)) {
      alert('Please select a valid image file!');

      this.selectedImage.set(null);
      this.imagePreview.set(null);

      input.value = '';
      return;
    }

    this.selectedImage.set(file);

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview.set(reader.result as string);
    };
    reader.readAsDataURL(file);

    input.value = '';
  }

  removeImage() {
    this.selectedImage.set(null);
    this.imagePreview.set(null);
  }


  createProduct() {
    const file = this.selectedImage();
    if (!file) return;

    const formData = new FormData();
    formData.append('name', this.product.name!);
    formData.append('description', this.product.description!);
    formData.append('price', this.product.price!.toString());
    formData.append('userId', this.product.userId!);
    formData.append('image', file);

    console.log('Submitting product with signal:', formData);
    this.close();
  }


  close() {
    this.closePopUp.emit();
  }
}
