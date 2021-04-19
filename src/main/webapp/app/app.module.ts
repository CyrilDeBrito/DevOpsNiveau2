import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { ExoDevopsSharedModule } from 'app/shared/shared.module';
import { ExoDevopsCoreModule } from 'app/core/core.module';
import { ExoDevopsAppRoutingModule } from './app-routing.module';
import { ExoDevopsHomeModule } from './home/home.module';
import { ExoDevopsEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    ExoDevopsSharedModule,
    ExoDevopsCoreModule,
    ExoDevopsHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    ExoDevopsEntityModule,
    ExoDevopsAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class ExoDevopsAppModule {}
