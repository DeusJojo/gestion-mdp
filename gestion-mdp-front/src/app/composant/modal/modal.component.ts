import { HttpClient } from '@angular/common/http';
import {
  Component,
  ElementRef,
  EventEmitter,
  Output,
  ViewChild,
} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Manager } from '../../model/Manager';
import { catchError, of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { Key } from '../../model/Key';
// @ts-ignore
const $: any = window['$'];

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [MatIconModule, FormsModule],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.css',
})
export class ModalComponent {
  @Output('getAllManagers') getAllManagers: EventEmitter<any> =
    new EventEmitter();

  @ViewChild('modal') modal?: ElementRef;

  public managerObj: Manager;
  private manager!: Manager;
  public title: string = '';
  public validButton: string = '';
  public validator: boolean = false;
  public validatorUsername: string = '';
  public validatorPassword: string = '';
  private key!: Key;

  private configUrlCreateManager: string =
    'http://localhost:8080/manager/create';

  private configUrlModifyManager: string =
    'http://localhost:8080/manager/modify/';

  constructor(private http: HttpClient) {
    this.managerObj = new Manager();
  }

  openModal(title: string, validButton: string, manager?: Manager) {
    this.title = title;
    this.validButton = validButton;
    if (manager) {
      this.managerObj = manager;
    }
    $(this.modal?.nativeElement).modal('show');
  }

  closeModal() {
    this.managerObj = new Manager();
    $(this.modal?.nativeElement).modal('hide');
    this.getAllManagers.emit();
  }

  modifyManager() {
    if (this.managerObj.passwordApp == '') {
      this.validator = true;
    } else {
      if (this.validator == true) {
        this.validator = false;
      }
      if (this.managerObj) {
        this.manager = this.managerObj;
      }
      Swal.fire({
        title: 'Êtes-vous sûr ?',
        color: '#fff',
        icon: 'warning',
        iconColor: '#0ef',
        showCancelButton: true,
        confirmButtonText: 'Modifer',
        cancelButtonText: 'Annuler',
        buttonsStyling: false,
        background: '#1f293a',
        customClass: {
          confirmButton: 'btn btn-outline-red right-margin',
          cancelButton: 'btn btn-outline-blue left-margin',
        },
      }).then((result) => {
        if (result.isConfirmed) {
          Swal.fire({
            title: 'Modifé !',
            text: 'Modification effectuée avec succés !',
            color: '#fff',
            icon: 'success',
            iconColor: '#0ef',
            background: '#1f293a',
            buttonsStyling: false,
            customClass: {
              confirmButton: 'btn btn-outline-blue',
            },
          });
          if (this.manager.usernameApp == '') {
            this.manager.usernameApp = 'Non renseigné';
          }
          this.http
            .put(this.configUrlModifyManager + this.manager.id, this.manager)
            .subscribe((res: any) => {
              if (res) {
                this.getAllManagers.emit();
              }
            });
        }
        this.closeModal();
      });
    }
  }

  addManager() {
    if (this.managerObj.passwordApp == '') {
      this.validator = true;
    } else {
      if (this.validator == true) {
        this.validator = false;
      }
      if (this.managerObj.usernameApp == '') {
        this.managerObj.usernameApp = 'Non renseigné';
      }
      this.http
        .post(this.configUrlCreateManager, this.managerObj)
        .pipe(
          catchError((e) => {
            console.error(e);
            return of(null);
          })
        )
        .subscribe((res: any) => {
          if (res) {
            this.getAllManagers.emit();
          }
        });
      this.closeModal();
    }
  }
}
