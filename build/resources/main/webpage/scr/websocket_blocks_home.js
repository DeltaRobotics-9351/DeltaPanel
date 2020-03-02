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
  socket.send("blocksProgramsList:7649");

};

socket.onmessage = function(event) {
  if(event.data === "pong"){
    socket.send("ping");
    ping = performance.now() - ping;

    if(nextPingUpdate < performance.now()){
        document.getElementById("ping_amount").innerHTML = "Ping: " + ping.toFixed(0) + " ms";
        nextPingUpdate = performance.now() + 1000;
    }

    ping = performance.now();

  }else if(event.data.startsWith("users:7649")){

    var split = event.data.split(":");
    document.getElementById("users_amount").innerHTML = "Connected clients: " + split[2];

    socket.send("users:7649");

  }else if(event.data.startsWith("blocksProgramsList:7649")){
        var split = event.data.split(":");
        var list = split[2].split(";");

        var s = '<table> <tr> <th>Name</th> <th>Last Modified</th> </tr>';

        list.forEach(element => {
            var splitElement = element.split(".a\\");

            var fileName = splitElement[0];

            var lastModified = splitElement[1];

            if(element !== ""){
            s += "<tr>";

            s += "<td>";

            s += fileName.replace(/.s\\/g, ";").replace(/.d\\/g, ":");

            s += "</td>";

            s += "<td>";

            s += lastModified.replace(/.d\\/g, ":");

            s += "</td>";

            s += "</tr>";

            }
        });

            s += "</table>";

            document.getElementById("blocksProgramsTable").innerHTML = s;

       socket.send("blocksProgramsList:7649");
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
    return performance.now       ||
        performance.mozNow    ||
        performance.msNow     ||
        performance.oNow      ||
        performance.webkitNow ||
        Date.now  /*none found - fallback to browser default */
})();

function sendCommand(command, id){
    socket.send(command + ":" + id);

    while(!receivedCommands.has(id)){ }

    return receivedCommands.get(id);

}

