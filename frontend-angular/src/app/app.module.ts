import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NotFoundPageComponent } from './not-found-page/not-found-page.component';
import { ProductsPageComponent } from './features/products/pages/products-page/products-page.component';
import { ProductListComponent } from './features/products/components/product-list/product-list.component';
import { ProductFormComponent } from './features/products/components/product-form/product-form.component';
import { ProductDetailComponent } from './features/products/components/product-detail/product-detail.component';
import { SalesPageComponent } from './features/sales/pages/sales-page/sales-page.component';
import { CartComponent } from './features/sales/components/cart/cart.component';
import { SaleFormComponent } from './features/sales/components/sale-form/sale-form.component';
import { StateMessageComponent } from './shared/components/state-message/state-message.component';

@NgModule({
  declarations: [
    AppComponent,
    ProductsPageComponent,
    ProductListComponent,
    ProductFormComponent,
    ProductDetailComponent,
    SalesPageComponent,
    CartComponent,
    SaleFormComponent,
    StateMessageComponent,
    NotFoundPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
