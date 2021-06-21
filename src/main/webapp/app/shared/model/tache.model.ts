export interface ITache {
  id?: number;
  nomTache?: string;
  description?: string;
}

export class Tache implements ITache {
  constructor(public id?: number, public nomTache?: string, public description?: string) {}
}
