
import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiClientError } from '../../../core/models/api-error.model';
import { SaleRequest, SaleResponse } from '../models/sale.model';
import { SaleService } from './sale.service';

describe('SaleService', () => {
  let service: SaleService;
  let httpMock: HttpTestingController;

  const endpoint = `${API_BASE_URL}/api/sales`;

  const saleRequest: SaleRequest = {
    P00001: 2
  };

  const saleResponse: SaleResponse = {
    lines: [
      {
        productCode: 'P00001',
        productName: 'Café',
        quantity: 2,
        unitPrice: 12.50,
        subtotal: 25.00
      }
    ],
    subtotal: 25.00,
    discount: 1.25,
    taxes: 3.80,
    total: 27.55
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SaleService]
    });

    service = TestBed.inject(SaleService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe procesar correctamente una venta', () => {
    service.processSale(saleRequest).subscribe(response => {
      expect(response).toEqual(saleResponse);
      expect(response.total).toBe(27.55);
    });

    const request = httpMock.expectOne(endpoint);

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(saleRequest);

    request.flush(saleResponse);
  });

  it('debe identificar un error HTTP 400 al procesar una venta', () => {
    let receivedError: ApiClientError | undefined;

    service.processSale(saleRequest).subscribe({
      error: (error: ApiClientError) => {
        receivedError = error;
      }
    });

    const request = httpMock.expectOne(endpoint);

    request.flush({
      title: 'Solicitud inválida',
      status: 400,
      detail: 'Las cantidades solicitadas no son válidas'
    }, {
      status: 400,
      statusText: 'Bad Request'
    });

    expect(receivedError?.kind).toBe('http');
    expect(receivedError?.status).toBe(400);
  });

  it('debe identificar un error HTTP 404 al procesar una venta', () => {
    let receivedError: ApiClientError | undefined;

    service.processSale(saleRequest).subscribe({
      error: (error: ApiClientError) => {
        receivedError = error;
      }
    });

    const request = httpMock.expectOne(endpoint);

    request.flush({
      title: 'Producto inexistente',
      status: 404,
      detail: 'No se encontró el producto solicitado'
    }, {
      status: 404,
      statusText: 'Not Found'
    });

    expect(receivedError?.kind).toBe('http');
    expect(receivedError?.status).toBe(404);
  });

  it('debe identificar un error HTTP 500 al procesar una venta', () => {
    let receivedError: ApiClientError | undefined;

    service.processSale(saleRequest).subscribe({
      error: (error: ApiClientError) => {
        receivedError = error;
      }
    });

    const request = httpMock.expectOne(endpoint);

    request.flush({
      title: 'Error interno del servidor',
      status: 500,
      detail: 'Ocurrió un error inesperado'
    }, {
      status: 500,
      statusText: 'Internal Server Error'
    });

    expect(receivedError?.kind).toBe('http');
    expect(receivedError?.status).toBe(500);
  });

  it('debe identificar un fallo de red al procesar una venta', () => {
    let receivedError: ApiClientError | undefined;

    service.processSale(saleRequest).subscribe({
      error: (error: ApiClientError) => {
        receivedError = error;
      }
    });

    const request = httpMock.expectOne(endpoint);

    request.error(new ProgressEvent('error'));

    expect(receivedError?.kind).toBe('network');
    expect(receivedError?.status).toBe(0);
  });
});
