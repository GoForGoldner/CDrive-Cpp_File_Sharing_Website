import {RxStompConfig} from "@stomp/rx-stomp";
import {environment} from '../../environments/environment';

export const prodRxStompConfig: RxStompConfig = {
  brokerURL: environment.API_WEBSOCKET_URL,

  // Configure heartbeat
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,

  // Reconnect settings
  reconnectDelay: 500,

  debug: (msg: string): void => {
    console.log("🧾 ", new Date(), msg);
  },
};
