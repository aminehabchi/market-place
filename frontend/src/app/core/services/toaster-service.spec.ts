import { describe, expect, it } from 'vitest';
import { ToasterService } from './toaster-service';

describe('ToasterService', () => {
  it('emits a success toast with the expected message', () => {
    const service = new ToasterService();

    service.success('Saved successfully');

    const toast = service.toaster$.getValue();
    expect(toast).not.toBeNull();
    expect(toast?.type).toBe('success');
    expect(toast?.message).toBe('Saved successfully');
  });

  it('emits an error toast when calling error()', () => {
    const service = new ToasterService();

    service.error('Something went wrong');

    const toast = service.toaster$.getValue();
    expect(toast).not.toBeNull();
    expect(toast?.type).toBe('error');
    expect(toast?.message).toBe('Something went wrong');
  });
});
