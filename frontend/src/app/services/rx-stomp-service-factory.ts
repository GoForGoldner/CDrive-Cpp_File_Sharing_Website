import { RxStompService } from './rx-stomp.service';
import { prodRxStompConfig } from './rx-stomp.prod.config';

export function rxStompServiceFactory(): RxStompService {
  const rxStomp = new RxStompService();
  rxStomp.configure(prodRxStompConfig);
  rxStomp.activate();

  if (rxStomp.active) {
    console.log("Websocket is active 🥰");
  } else {
    console.log("Websocket inactive 😔");
  }

  return rxStomp;
}
