import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { ModalComponent } from '../../composant/modal/modal.component';
import Swal from 'sweetalert2';
import { Manager } from '../../model/Manager';
import { Router } from '@angular/router';
import { Observable, Subject, catchError, throwError } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  imports: [MatIconModule, ModalComponent],
  providers: [ModalComponent],
})
export class DashboardComponent implements OnInit {
  @ViewChild(ModalComponent) modal?: ModalComponent;

  title: string = '';
  validButton: string = '';
  isEncrypt: boolean = true;

  private configUrlGetAllUserManager: string =
    'http://localhost:8080/manager/getByUser';
  private configUrlGetManagerDecrypt: string =
    'http://localhost:8080/manager/getByIdDecrypt';
  private configUrlGetManager: string =
    'http://localhost:8080/manager/getById';
  private configUrlDeleteManager: string =
    'http://localhost:8080/manager/delete/';

  managers: any[] = [];

  constructor(private Http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.getAllManagers();
  }

  open(title: string, validButton: string, manager?: Manager) {
    if (manager) {
      this.decryptOne(manager).subscribe((decryptedManager: Manager) => {
        this.title = title;
        this.validButton = validButton;
        this.modal?.openModal(title, validButton, decryptedManager);
      });
    } else {
      this.title = title;
      this.validButton = validButton;
      this.modal?.openModal(title, validButton, manager);
    }
  }
  

  getAllManagers() {
    this.Http.get<any[]>(this.configUrlGetAllUserManager).subscribe(
      (res: any[]) => {
        this.managers = res.map((manager) => ({
          ...manager,
          isEncrypt: true,
        }
      ));
      }
    );
  }

  decryptOne(manager?: Manager): Observable<Manager> {
    if (!manager) {
      throw new Error('Manager must be provided');
    }
    return this.Http.get<Manager>(`${this.configUrlGetManagerDecrypt}/${manager.id}`);
  }
  

  decryptAll(id?: bigint) {
    this.Http.get(this.configUrlGetManagerDecrypt + '/' + id).subscribe(
      (res: any) => {
        const decryptedManagerIndex = this.managers.findIndex(
          (manager) => manager.id === res.id
        );

        if (decryptedManagerIndex !== -1) {
          this.managers[decryptedManagerIndex] = res;
          this.managers[decryptedManagerIndex].isEncrypt = false;
        } else {
          console.error('Manager introuvable dans la liste des managers.');
        }
      }
    );
  }

  encrypt(id: bigint) {
    this.Http.get(this.configUrlGetManager + '/' + id).subscribe((res: any) => {
      const encryptedManagerIndex = this.managers.findIndex(
        (manager) => manager.id === res.id
      );

      if (encryptedManagerIndex !== -1) {
        this.managers[encryptedManagerIndex] = res;
        this.managers[encryptedManagerIndex].isEncrypt = true;
      } else {
        console.error('Manager introuvable dans la liste des managers.');
      }
    });
  }

  deleteManager(id: bigint) {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: 'La suppression est définitive !',
      color: '#fff',
      icon: 'warning',
      iconColor: '#0ef',
      showCancelButton: true,
      confirmButtonText: 'Supprimer',
      cancelButtonText: 'Annuler',
      buttonsStyling: false,
      background: '#1f293a',
      customClass: {
        confirmButton: 'btn btn-outline-red right-margin',
        cancelButton: 'btn btn-outline-blue left-margin',
      },
    }).then((result) => {
      if (result.isConfirmed) {
        this.Http.delete(this.configUrlDeleteManager + id)
          .pipe(
            catchError((error: HttpErrorResponse) => {
              if (error.status == 403) {
                // Gérer l'erreur
                alert(
                  "Accès refusé. Vous n'êtes pas autorisé à effectuer cette action."
                );
              }
              // Renvoyer une erreur observable pour permettre à d'autres parties du code de gérer l'erreur si nécessaire
              return throwError(
                () =>
                  "Une erreur s'est produite lors de la suppression. Veuillez réessayer."
              );
            })
          )
          .subscribe((res: any) => {
            Swal.fire({
              title: 'Supprimé !',
              text: 'Supression effectuée avec succés !',
              color: '#fff',
              icon: 'success',
              iconColor: '#0ef',
              background: '#1f293a',
              buttonsStyling: false,
              customClass: {
                confirmButton: 'btn btn-outline-blue',
              },
            });
            this.getAllManagers();
          });
      }
    });
  }
}
