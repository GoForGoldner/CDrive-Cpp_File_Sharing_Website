// websocket.service.ts
import {Injectable, OnDestroy} from '@angular/core';
import {rxStompServiceFactory} from './rx-stomp-service-factory';
import {toSignal} from '@angular/core/rxjs-interop';
import {RxStompState} from '@stomp/rx-stomp';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService implements OnDestroy {
  public output: string = "";
  private rxStompService = rxStompServiceFactory();
  public connectionState = toSignal(this.rxStompService.connectionState$, {initialValue: RxStompState.CLOSED});

  constructor() {
    // Subscribe to compiler output
    this.rxStompService.watch('/user/queue/compiler-output').subscribe((message: any) => {
      console.log('Received compiler output:', message.body);
      if (message.body != "") {
        this.output += message.body;
      }
    });

    this.rxStompService.watch('/user/queue/errors').subscribe((message: any) => {
      console.log('Received error output:', message.body);
      if (message.body != "") {
        this.output += message.body;
      }
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

  public disconnect(): void {
    // This should only be called when the application is shutting down
    console.log('Disconnecting WebSocket service');
    this.rxStompService.deactivate();
  }

  // Clean up when service is destroyed
  ngOnDestroy(): void {
    this.disconnect();
  }
}
