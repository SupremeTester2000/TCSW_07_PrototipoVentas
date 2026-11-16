import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductsPageComponent } from './features/products/pages/products-page/products-page.component';
import { SalesPageComponent } from './features/sales/pages/sales-page/sales-page.component';
import { NotFoundPageComponent } from './not-found-page/not-found-page.component';

const routes: Routes = [
  { path: '', redirectTo: 'products', pathMatch: 'full' },
  { path: 'products', component: ProductsPageComponent, data: { title: 'Productos' } },
  { path: 'sales', component: SalesPageComponent, data: { title: 'Ventas' } },
  { path: 'not-found', component: NotFoundPageComponent, data: { title: 'Página no encontrada' } },
  { path: '**', redirectTo: 'not-found' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
