let socket = new WebSocket("ws://"+location.hostname+":51");
let hasConnected = false
let ping = 0;

let nextPingUpdate;

var receivedCommands = new Map();

var isSocketOpened = false;

socket.onopen = function(e) {
  isSocketOpened = true;

  ping = performance.now();

  nextPingUpdate = performance.now() + 1000;

  document.getElementById("socket_status").innerHTML = "Connected to the server.";

  socket.send("users:7649");
};

socket.onmessage = function(event) {


  if(event.data === "pong"){ socket.send("ping:gamepad"); }

  if(event.data === "pong:gamepad"){
    socket.send("ping:gamepad");
    ping = performance.now() - ping;

    if(nextPingUpdate < performance.now()){
        document.getElementById("ping_amount").innerHTML = "Ping: " + ping.toFixed(0) + " ms";
        nextPingUpdate = performance.now() + 1000;
    }

    ping = performance.now();

  }else if(event.data.startsWith("users:7649")){

    var split = event.data.split(":");
    document.getElementById("users_amount").innerHTML = "Connected clients: " + split[2];

    setTimeout(function(){ socket.send("users:7649"); }, 2000);

  }else if(event.data === "close:alreadyclient"){

    socket.close();
    confirm("There is another client already connected to the Control Panel.\nTo avoid instability, you have been disconnected from the server.\nYou'll be redirected to the home page.");

    window.location.href = "/home.html";

  }else{


    var split = event.data.split(":");

    //receivedCommands.put(split[1], split[2]);

  }
};

socket.onclose = function(event) {

  ping = 0;
  document.getElementById("ping_amount").innerHTML = "Ping: ?";

  isSocketOpened = false;

  if (event.wasClean) {
    document.getElementById("socket_status").innerHTML = "Disconnected.";
  } else {
    // e.g. server process killed or network down
    // event.code is usually 1006 in this case
    document.getElementById("socket_status").innerHTML = "Disconnected unexpectedly";
    location.reload();
  }

};

socket.onerror = function(error) {
    document.getElementById("socket_status").innerHTML = "Error: " + error.message;
};

window.bind('unload', function(e){
  socket.close();
});

window.performance = window.performance || {};
performance.now = (function() {
    return round(performance.now       ||
        performance.mozNow    ||
        performance.msNow     ||
        performance.oNow      ||
        performance.webkitNow ||
        Date.now)  /*none found - fallback to browser default */
})();

function sendCommand(command, id){

    socket.send(command + ":" + id);

    while(!receivedCommands.has(id)){ }

    return receivedCommands.get(id);

}

function buttonPressed(b) {
  if (typeof(b) == "object") {
    return b.pressed;
  }
  return b == 1.0;
}


var startAIndex = -1;
var startBIndex = -1;

var startAData = "";
var startBData = "";

function gamepadLoop() {
  var gamepads = navigator.getGamepads ? navigator.getGamepads() : (navigator.webkitGetGamepads ? navigator.webkitGetGamepads : []);

  startAData = "unlinked";
  startBData = "unlinked";

  for(gi in gamepads){

      var gp = gamepads[gi]; //get gamepad

      if(gp.buttons[9].pressed && gp.buttons[0].pressed){ //check if (+) & (A) buttons are pressed
            console.log("Start A logged in");
            linkStartA(gi); //link gamepad to start a

            if(startBIndex === gi){ unlinkStartB(); } //if the gamepad was start a, unlink it from start a
      }

      if(gp.buttons[9].pressed && gp.buttons[1].pressed){ //check if (+) & (B) buttons are pressed
            console.log("Start B logged in");
            linkStartB(gi); //link gamepad to start b

            if(startAIndex === gi){ unlinkStartA(); }  //if the gamepad was start a, unlink it from start a
      }


      if(gi === startAIndex){ //parse Start A buttons to a String

            var triggerData = "";

            startAData = "";
            for(gbi in gp.buttons){
                var gb = gp.buttons[gbi];

                if(gbi == 0){
                    startAData += gb.pressed;
                }else{
                    startAData += "," + gb.pressed;
                }

                if(gbi == 6){
                    triggerData += gb.value.toString();
                }else if(gbi == 7){
                    triggerData += "," + gb.value.toString();
                }
            }

            startAData += ";";

            for(gai in gp.axes){ //axes
               var ga = gp.axes[gai]; //get axis from array

               if(gai == 0){
                    startAData += ga.toString();
               }else{
                   startAData += "," + ga.toString();
               }
            }

             startAData += ";" + triggerData;
      }

      if(gi === startBIndex){ //parse Start B buttons & axes to a String

            var triggerData = "";

            startBData = "";
            for(gbi in gp.buttons){ //buttons
                 var gb = gp.buttons[gbi]; //get button from array

                 if(gbi == 0){
                    startBData += gb.pressed;
                 }else{
                    startBData += "," + gb.pressed;
                 }

                 if(gbi == 6){
                    triggerData += gb.value.toString();
                 }else if(gbi == 7){
                    triggerData += "," + gb.value.toString();
                 }
            }

            startBData += ";";

            for(gai in gp.axes){ //axes
                 var ga = gp.axes[gai]; //get axis from array

                 if(gai == 0){
                    startBData += ga.toString();
                 }else{
                    startBData += "," + ga.toString();
                 }

            }

            startBData += ";" + triggerData;

       }
  }
      socket.send("gamepadA:" + startAData);
      socket.send("gamepadB:" + startBData);
}

window.addEventListener("gamepaddisconnected", function(e){

    if(e === startAIndex){ unlinkStartA(); }
    if(e === startBIndex){ unlinkStartB(); }

});

window.onbeforeunload = function() {
      socket.send("gamepadA:unlinked");
      socket.send("gamepadB:unlinked");
      //socket.close();
};

function linkStartA(index){
    document.getElementById("StartA").innerHTML = "Start A: Linked";
    startAIndex = index;
}

function unlinkStartA(){
    document.getElementById("StartA").innerHTML = "Start A: Not Linked";
    startAIndex = -1;
}

function linkStartB(index){
    document.getElementById("StartB").innerHTML = "Start B: Linked";
    startBIndex = index;
}

function unlinkStartB(){
    document.getElementById("StartB").innerHTML = "Start B: Not Linked";
    startBIndex = -1;
}