// websocket.service.ts
import { Injectable, OnDestroy } from '@angular/core';
import { RxStomp, RxStompState } from '@stomp/rx-stomp';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { rxStompServiceFactory } from './rx-stomp-service-factory';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService implements OnDestroy {
  private rxStompService: RxStomp;
  private connectionStatus = new BehaviorSubject<boolean>(false);

  public output: string = "";

  constructor() {
    // Create the service once and keep it alive
    this.rxStompService = rxStompServiceFactory();

    // Track connection state
    this.rxStompService.connectionState$.subscribe(state => {
      const stateString =
        state === 0 ? 'CONNECTING' :
          state === 1 ? 'OPEN' :
            state === 2 ? 'CLOSING' :
              state === 3 ? 'CLOSED' : 'UNKNOWN';
      console.log(`WebSocket state: ${state} (${stateString})`);
    });

    // Subscribe to compiler output
    this.rxStompService.watch('/topic/compiler-output').subscribe((message: any) => {
      console.log('Received compiler output:', message.body);
      this.output += message.body + "\n";
    });

    this.rxStompService.watch('/user/queue/compiler-output').subscribe((message: any) => {
      console.log('Received compiler output:', message.body);
      this.output += message.body + "\n";
    });

    this.rxStompService.watch('/user/queue/errors').subscribe((message: any) => {
      console.log('Received error output:', message.body);
      this.output += message.body + "\n";
    });
  }

  public executeFile(fileId: number): void {
    if (!this.rxStompService.active) {
      console.log("Websocket when executing file wasn't active 😔");
      return;
    }

    console.log('Sending execute request for file ID:', fileId);
    this.rxStompService.publish({
      destination: '/app/execute-cpp',
      body: fileId.toString()
    });

    console.log("Message sent!");
  }

  public sendInputMessage(line: string) {
    // Add the line to simulate the terminal
    this.output += line + "\n";

    if (!this.rxStompService.active) {
      console.log("Websocket when executing file wasn't active 😔");
      return;
    }

    this.rxStompService.publish({
      destination: '/app/cpp-input',
      body: line.toString()
    });

    console.log("Input message sent!");
  }

  public disconnect() {
    // This should only be called when the application is shutting down
    console.log('Disconnecting WebSocket service');
    this.rxStompService.deactivate();
  }

  // Clean up when service is destroyed
  ngOnDestroy() {
    this.disconnect();
  }
}