import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class MediaSevice {
  isImage(file: File): boolean {
    return file.type.startsWith('image/');
  }
}
