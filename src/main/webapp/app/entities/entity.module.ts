import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tache',
        loadChildren: () => import('./tache/tache.module').then(m => m.ExoDevopsTacheModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class ExoDevopsEntityModule {}
