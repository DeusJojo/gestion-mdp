export class Manager {
  id?: bigint
  passwordApp: string;
  usernameApp: string;
  isEncrypt!: boolean;

  constructor() {
    this.id;
    this.passwordApp = '';
    this.usernameApp = '';
    this.isEncrypt;
  }

  public set setPasswordApp(value: string){
    this.passwordApp = value;
  }
}
