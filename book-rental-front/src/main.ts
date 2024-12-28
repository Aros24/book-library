import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { environment } from './app/environment/environment';

if (environment.production) {
  window.console.log = () => {};
  window.console.warn = () => {};
  window.console.error = () => {};
  window.alert = () => {};
}

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));