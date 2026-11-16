
import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiClientError } from '../../../core/models/api-error.model';
import { ProductRequest, ProductResponse } from '../models/product.model';
import { ProductService } from './product.service';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  const endpoint = `${API_BASE_URL}/api/products`;

  const product: ProductResponse = {
    code: 'P00001',
    name: 'Café',
    price: 12.50,
    stock: 4
  };

  const productRequest: ProductRequest = {
    name: 'Café',
    price: 12.50,
    stock: 4
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService]
    });

    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe consultar correctamente el catálogo', () => {
    service.getProducts().subscribe(products => {
      expect(products).toEqual([product]);
    });

    const request = httpMock.expectOne(endpoint);

    expect(request.request.method).toBe('GET');
    request.flush([product]);
  });

  it('debe manejar un catálogo vacío', () => {
    service.getProducts().subscribe(products => {
      expect(products).toEqual([]);
      expect(products.length).toBe(0);
    });

    const request = httpMock.expectOne(endpoint);

    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('debe registrar un producto y aceptar HTTP 201', () => {
    service.createProduct(productRequest).subscribe(response => {
      expect(response).toEqual(product);
    });

    const request = httpMock.expectOne(endpoint);

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(productRequest);

    request.flush(product, {
      status: 201,
      statusText: 'Created'
    });
  });

  it('debe actualizar un producto existente', () => {
    service.updateProduct('P00001', productRequest).subscribe(response => {
      expect(response).toEqual(product);
    });

    const request = httpMock.expectOne(`${endpoint}/P00001`);

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(productRequest);

    request.flush(product);
  });

  it('debe identificar un error HTTP 400', () => {
    let receivedError: ApiClientError | undefined;

    service.createProduct(productRequest).subscribe({
      error: (error: ApiClientError) => {
        receivedError = error;
      }
    });

    const request = httpMock.expectOne(endpoint);

    request.flush({
      title: 'Solicitud inválida',
      status: 400,
      detail: 'Revise los siguientes campos: name, price'
    }, {
      status: 400,
      statusText: 'Bad Request'
    });

    expect(receivedError?.kind).toBe('http');
    expect(receivedError?.status).toBe(400);
    expect(receivedError?.title).toBe('Solicitud inválida');
  });

  it('debe identificar un error HTTP 404', () => {
    let receivedError: ApiClientError | undefined;

    service.updateProduct('P99999', productRequest).subscribe({
      error: (error: ApiClientError) => {
        receivedError = error;
      }
    });

    const request = httpMock.expectOne(`${endpoint}/P99999`);

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
    expect(receivedError?.detail).toBe(
      'No se encontró el producto solicitado'
    );
  });

  it('debe identificar un error HTTP 500', () => {
    let receivedError: ApiClientError | undefined;

    service.getProducts().subscribe({
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

  it('debe diferenciar un fallo de red de un error HTTP', () => {
    let receivedError: ApiClientError | undefined;

    service.getProducts().subscribe({
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
