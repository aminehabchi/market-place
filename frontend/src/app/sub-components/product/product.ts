import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { Product } from '../../core/models/Product';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductsService } from '../../core/services/products-service';
import { FormsModule } from '@angular/forms';
import { MediaSevice } from '../../core/services/media-sevice';

@Component({
  selector: 'app-product',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product.html',
  styleUrls: ['./product.css'],
})
export class ProductItem {
  @Input() product!: Product;
  updatedProduct!: Product;
  @Output() deletedProductId = new EventEmitter<string>();

  // Signals for image preview and selected file
  imagePreview = signal<string | null>(null);
  selectedImage = signal<File | null>(null);

  isMyProduct: boolean = false;
  isEditing = false;

  constructor(private router: Router, private producteService: ProductsService, private mediaSevice: MediaSevice) { }

  ngOnInit() {
    this.updatedProduct = structuredClone(this.product);
    const currentUrl = this.router.url;
    this.isMyProduct = currentUrl === '/dashboard';
  }

  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    // Optional: validate image type
    if (!file.type.startsWith('image/')) {
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
      this.updatedProduct.image = reader.result as string;
    };
    reader.readAsDataURL(file);

    input.value = '';
  }


  save() {
    const file = this.selectedImage();

    if (file) {
      // Step 1: Upload image
      this.mediaSevice.uploadProductImage(file).subscribe({
        next: (imageUrl: any) => {
          console.log(imageUrl);
          
          // Step 2: Set the uploaded image URL in the updated product
          this.updatedProduct.image = imageUrl;

          // Step 3: Update the product with new image
          this.producteService.updateProduct(this.product.id, this.updatedProduct).subscribe({
            next: () => {
              this.product = structuredClone(this.updatedProduct);
              this.closeUpdate();
            },
            error: (err) => {
              console.error('Error updating product:', err);
              alert('Failed to update product. Please try again.');
            }
          });
        },
        error: (err) => {
          console.error('Error uploading image:', err);
          alert('Failed to upload image. Please try again.');
        }
      });
    } else {
      // No new image selected, just update the product
      this.producteService.updateProduct(this.product.id, this.updatedProduct).subscribe({
        next: () => {
          this.product = structuredClone(this.updatedProduct);
          this.closeUpdate();
        },
        error: (err) => {
          console.error('Error updating product:', err);
          alert('Failed to update product. Please try again.');
        }
      });
    }
  }

  cancel() {
    this.updatedProduct = structuredClone(this.product);
    this.closeUpdate()
  }

  closeUpdate() {
    this.isEditing = false
    this.selectedImage.set(null);
    this.imagePreview.set(null);
  }

  delete() {
    if (!confirm('Are you sure you want to delete this product?')) return;

    this.producteService.deleteProducts(this.product.id).subscribe({
      next: () => {
        alert('Product deleted successfully!');
        this.deletedProductId.emit(this.product.id); // emit after success
      },
      error: (err) => {
        console.error('Error deleting product:', err);
        alert('Failed to delete product. Please try again.');
      },
    });
  }
}
