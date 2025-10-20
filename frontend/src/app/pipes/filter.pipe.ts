import { Pipe, PipeTransform } from '@angular/core';
import { Car } from '../models/car.model';

@Pipe({
  name: 'filter',
  standalone: true
})
export class FilterPipe implements PipeTransform {
  transform(cars: Car[], status: string): Car[] {
    if (status === 'active') {
      return cars.filter(car => !car.dataSaida);
    } else if (status === 'exited') {
      return cars.filter(car => car.dataSaida);
    }
    return cars;
  }
}
