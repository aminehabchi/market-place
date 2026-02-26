export type ToastType = 'info' | 'error' | 'success' | 'warning';

export class ToastMessage {
    constructor(public type: ToastType,public message: string) { }
}