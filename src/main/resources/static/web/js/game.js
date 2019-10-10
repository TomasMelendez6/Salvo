/*
SALVO GAME 'Game View' JS Code
developed by Berenguer Pou / Ubiqum Barcelona (berenguer@ubiqum.com)
Last update: June, 11, 2018
*/


var gamePlayerData = {};
var errorMsg;
var you = "";
var viewer = "";
var youID = "";
var salvoJSON;
var salvoPositions = [];
var waitState = false;

refreshGameView(makeUrl());

$('#logoutButton').on('click', function (event) {
    event.preventDefault();
    $.post("/api/logout")
        .done(function () {
            console.log("logout ok");
            $('#logoutSuccess').show("slow").delay(2000).hide("slow");
            setTimeout(
                function()
                {
                    location.href = "/web/games.html";
                }, 3000);
        })
        .fail(function () {
            console.log("logout fails");
        })
        .always(function () {

        });
});

function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function makeUrl() {
    var gamePlayerID =  getParameterByName("gp");
    return '/api/game_view/' + gamePlayerID;
}

function makePostUrl() {
    var gamePlayerID =  getParameterByName("gp");
    return '/api/games/players/' + gamePlayerID + '/ships';
}

function makePostUrlSalvoes() {
    var gamePlayerID =  getParameterByName("gp");
    return '/api/games/players/' + gamePlayerID + '/salvoes';
}

function refreshGameView(_url) {
    $.ajax({
        url: _url,
        type: 'GET',
        success: function (data) {
            gamePlayerData = data;
            // console.log(gamePlayerData);
            // createTable(player1);
            // createTable(player2);



            $('#gameStateBlock').html('<span class="gameStateLabel">TURN: </span><span class="gameStateLabelBig">' + getTurn(gamePlayerData) + '</span><span class="gameStateLabel"> ACTION REQUIRED: </span><span class="gameStateLabelBig">' + gamePlayerData.gameState + '</span>');

            console.log("waitState: " + waitState);

            if (waitState === false) {
                showSelf(gamePlayerData);
                makeGameRecordTable(gamePlayerData.hits.opponent, "gameRecordOppTable");
                makeGameRecordTable(gamePlayerData.hits.self, "gameRecordSelfTable");
            }

            if (gamePlayerData.gameState === "PLACESHIPS"){
                $('#placingShipsBoard').show('puff', 'slow');
            }

            if (gamePlayerData.gameState === "WAITINGFOROPP"){
                $('#battleGrids').show('puff', 'slow');
                waitState = true;
                setTimeout(
                    function()
                    {
                        refreshGameView(makeUrl());
                        console.log("...refreshing gameview...");

                    }, 5000);
            }

            if (gamePlayerData.gameState === "WON"){
                showSelf(gamePlayerData);
                makeGameRecordTable(gamePlayerData.hits.opponent, "gameRecordOppTable");
                makeGameRecordTable(gamePlayerData.hits.self, "gameRecordSelfTable");
                $('#battleGrids').show('puff', 'slow');
                $('#gameRecordBlock').show('puff', 'slow');
                console.log("yes you won");
            }
            if (gamePlayerData.gameState === "TIE"){
                showSelf(gamePlayerData);
                makeGameRecordTable(gamePlayerData.hits.opponent, "gameRecordOppTable");
                makeGameRecordTable(gamePlayerData.hits.self, "gameRecordSelfTable");
                $('#battleGrids').show('puff', 'slow');
                $('#gameRecordBlock').show('puff', 'slow');
                console.log("TIED MATCH");
            }
            if (gamePlayerData.gameState === "LOST"){
                showSelf(gamePlayerData);
                makeGameRecordTable(gamePlayerData.hits.opponent, "gameRecordOppTable");
                makeGameRecordTable(gamePlayerData.hits.self, "gameRecordSelfTable");
                $('#battleGrids').show('puff', 'slow');
                $('#gameRecordBlock').show('puff', 'slow');
                console.log("OH YOU LOST");
            }
            if (gamePlayerData.gameState === "WAIT"){
                $('#battleGrids').show('puff', 'slow');
                $('#salvoBlock').hide('puff', 'slow');
                $('#gameRecordBlock').show('puff', 'slow');
                waitState = true;
                setTimeout(
                    function()
                    {
                        refreshGameView(makeUrl());
                        console.log("...refreshing gameview...");

                    }, 5000);
            }
            if (gamePlayerData.gameState == "PLAY"){
                showSelf(gamePlayerData);
                makeGameRecordTable(gamePlayerData.hits.opponent, "gameRecordOppTable");
                makeGameRecordTable(gamePlayerData.hits.self, "gameRecordSelfTable");

                $('#salvoBlock').html('<div class="drag-zone">\n' +
                    '                <div class="droppable salvoCharger caught--it" id="salvoout1"><div class="draggable" id="salvo1"></div></div>\n' +
                    '                <div class="droppable salvoCharger caught--it" id="salvoout2"><div class="draggable" id="salvo2"></div></div>\n' +
                    '                <div class="droppable salvoCharger caught--it" id="salvoout3"><div class="draggable" id="salvo3"></div></div>\n' +
                    '                <div class="droppable salvoCharger caught--it" id="salvoout4"><div class="draggable" id="salvo4"></div></div>\n' +
                    '                <div class="droppable salvoCharger caught--it" id="salvoout5"><div class="draggable" id="salvo5"></div></div>\n' +
                    '                <div class="textCenter"><button class="btn btn-warning" id="postSalvo">Fire Salvo!</button></div>\n' +
                    '            </div>');

                resetSalvoCellIds();

                $('#postSalvo').click(function () {
                    makeSalvoJSON();
                    if (salvoPositions.length === 0){
                        $('#errorSalvo').text("Error! No salvos to fire! You must set at least one target!");
                        $('#errorSalvo').show( "slow" ).delay(3000).hide( "slow" );
                        console.log("No salvos to shoot!");
                    } else {
                        postSalvo(makePostUrlSalvoes());
                    }
                });

                $('#battleGrids').show('puff', 'slow');
                $('#salvoBlock').show('puff', 'slow');
                $('#gameRecordBlock').show('puff', 'slow');
            }

        },
        error: function(e){
            console.log(e);
            errorMsg = e.responseJSON.error;
            console.log(errorMsg);
            $('#errorMsg').text("Error: " + errorMsg);
            $('#errorMsg').show( "slow" ).delay(5000).hide( "slow" );
        }
    });
}

function showSelf (gamePlayerData) {
    you = "";
    viewer = "";
    youID = "";

    gamePlayerData.gamePlayers.forEach(function(gamePlayer) {
        if (gamePlayer.id == getParameterByName("gp")) {
            you = gamePlayer.player.email;
            youID = gamePlayer.player.id;
        } else {
            viewer = gamePlayer.player.email;
            $('#OpponentPlayerName').removeClass('waitingPlayer');
        }
    });

    if (viewer === "") {
        viewer = "Waiting for player!";
        $('#OpponentPlayerName').addClass('waitingPlayer');
    }

    let DateCreated = new Date(gamePlayerData.created);
    DateCreated = DateCreated.getMonth() + 1 + "/" + DateCreated.getDate() + " " + DateCreated.getHours() + ":" + DateCreated.getMinutes();
    $('#gamePlayerDetails').html('<span class="labelGame">Game ID: </span><span class="labelGameBig">' + gamePlayerData.id + '</span><span class="labelGame"> Created: </span><span class="labelGameBig">' + DateCreated + '</span>');
    $('#currentPlayerName').text(you);
    $('#OpponentPlayerName').text(viewer);


    gamePlayerData.ships.forEach(function(ship) {

        let firstCellID;
        firstCellID = "#p1_" + ship.locations[0];
        if (ship.locations[0].substring(1) === ship.locations[1].substring(1)) {
            $(firstCellID).html('<img class="shipsImgOnSelfGridVer" src="img/' + ship.type + 'ver.png">');
                } else {
            $(firstCellID).html('<img class="shipsImgOnSelfGridHor" src="img/' + ship.type + 'hor.png">');
        }
        // console.log(ship.type);
        ship.locations.forEach(function(location) {
            var cellID = "#p1_" + location;
            $(cellID).addClass("shipCell");
       //     console.log(location);
        });
    });

    gamePlayerData.salvoes.forEach(function(salvo) {

      //  console.log("Turn: " + salvo.turn);
        salvo.locations.forEach(function(location) {
            var cellID;
            if (salvo.player == youID){
                cellID = "#" + location;
                $(cellID).addClass("salvoCell");

        //        console.log("Your salvo on " + location);
                $(cellID).text(salvo.turn);
            } else {
                cellID = "#p1_" + location;
                if ($(cellID).hasClass("shipCell")) {
                    $(cellID).addClass("hitCell");

          //          console.log("Opponent Hits Ship on " + location);
                } else {
                    $(cellID).addClass("salvoCellSelf");
                    $(cellID).text(salvo.turn);
          //          console.log("Opponent salvo on " + location);
                }
            }

        });
    });

    gamePlayerData.hits.opponent.forEach(function(playTurn) {
        playTurn.hitLocations.forEach(function (hitCell) {
            cellID = "#" + hitCell;
            $(cellID).addClass("hitCell");
        });
    });
}

function createTable(player) {
    var prova = 0;
    var l = 0;
    var gridLabel;
    var gridId;
    if (player == "p1_") {
        gridLabel = $('<p class="gridLabel">Self grid</p>');
        gridId = "#p1Grid";
    } else {
        gridLabel = $('<p class="gridLabel">Opponent grid</p>');
        gridId = "#p2Grid";
    }
    var mytable = $('<table></table>').attr({
        id: "basicTable",
        class: ""
    });
    var rows = 10;
    var cols = 10;
    var tr = [];

    for (var i = 0; i <= rows; i++) {
        var row = $('<tr></tr>').attr({
            class: ["class1"].join(' ')
        }).appendTo(mytable);
        if (i == 0) {
            for (var j = 0; j < cols + 1; j++) {
                $('<th></th>').text(j).attr({
                    class: ["info"]
                }).appendTo(row);
            }
        } else {
            for (var j = 0; j < cols; j++) {
                if (j == 0) {
                    $('<th></th>').text(String.fromCharCode(65+(l++))).attr({
                        class: ["info"]
                    }).appendTo(row);
                }
                $('<td></td>').attr('id', player + String.fromCharCode(65+(i-1)) + "" + (j+1)).appendTo(row);

            }
        }
    }

    gridLabel.appendTo(gridId);
    mytable.appendTo(gridId);
}

function postShipLocations (postUrl) {
    $.post({
        url: postUrl,
        data: shipsJSON,
        dataType: "text",
        contentType: "application/json"
    })
        .done(function (response) {
            console.log(response);
            $('#okShips').text(JSON.parse(response).OK);
            $('#okShips').show( "slow" ).delay(3000).hide( "slow" );
            setTimeout(
                function()
                {
                    $('#placingShipsBoard').hide("slow");
                    refreshGameView(makeUrl());

                }, 4000);

        })
        .fail(function (response) {
            console.log(response);
            $('#errorShips').text(JSON.parse(response.responseText).error);
            $('#errorShips').show( "slow" ).delay(4000).hide( "slow" );
        })
}

function postSalvo (postUrl) {
    $.post({
        url: postUrl,
        data: salvoJSON,
        dataType: "text",
        contentType: "application/json"
    })
        .done(function (response) {
            console.log(response);
            $('#okSalvo').text(JSON.parse(response).OK);
            $('#okSalvo').show( "slow" ).delay(3000).hide( "slow" );
            $('#salvoBlock').hide("slow");
            $('.oppCell').removeClass('caught--it');
            $('#salvoBlock').empty();
            waitState = false;

            setTimeout(
                function()
                {
                    refreshGameView(makeUrl());
                }, 4000);
        })
        .fail(function (response) {
            console.log(response);
            $('#errorSalvo').text(JSON.parse(response.responseText).error);
            $('#errorSalvo').show( "slow" ).delay(4000).hide( "slow" );
        })
}

function displayOverlay(text) {
    $("<table id='overlay'><tbody><tr><td>" + text + "<br><button class='btn btn-info' onclick='removeOverlay()'>Ok! I got it.</button> </td></tr></tbody></table>").css({
        "position": "absolute",
        "margin": "auto",
        "top": "0",
        "right": "0",
        "left": "0",
        "bottom": "0",
        "width": "80%",
        "height": "200px",
        "background-color": "rgba(255,0,0,.89)",
        "z-index": "10000",
        "vertical-align": "middle",
        "text-align": "center",
        "color": "#fff",
        "font-size": "24px"
    }).appendTo(".gridShips").show( "slow" );
}

function removeOverlay() {

    $("#overlay").hide('puff', 'slow', function(){ $("#overlay").remove(); });
}

function makeSalvoJSON() {
    salvoPositions = [];
    salvoObject = {};
    if (salvo1cellID !== "salvoout1" && salvo1cellID !== "salvoout2" && salvo1cellID !== "salvoout3" && salvo1cellID !== "salvoout4" && salvo1cellID !== "salvoout5") {
        salvoPositions.push(salvo1cellID);
    }
    if (salvo2cellID !== "salvoout1" && salvo2cellID !== "salvoout2" && salvo2cellID !== "salvoout3" && salvo2cellID !== "salvoout4" && salvo2cellID !== "salvoout5") {
        salvoPositions.push(salvo2cellID);
    }
    if (salvo3cellID !== "salvoout1" && salvo3cellID !== "salvoout2" && salvo3cellID !== "salvoout3" && salvo3cellID !== "salvoout4" && salvo3cellID !== "salvoout5") {
        salvoPositions.push(salvo3cellID);
    }
    if (salvo4cellID !== "salvoout1" && salvo4cellID !== "salvoout2" && salvo4cellID !== "salvoout3" && salvo4cellID !== "salvoout4" && salvo4cellID !== "salvoout5") {
        salvoPositions.push(salvo4cellID);
    }
    if (salvo5cellID !== "salvoout1" && salvo5cellID !== "salvoout2" && salvo5cellID !== "salvoout3" && salvo5cellID !== "salvoout4" && salvo5cellID !== "salvoout5") {
        salvoPositions.push(salvo5cellID);
    }
    salvoObject = {
        salvoLocations : salvoPositions
    }

    salvoJSON = JSON.stringify(salvoObject);
    console.log(salvoJSON);
}

function makeGameRecordTable (hitsArray, gameRecordTableId) {

    var tableId = "#" + gameRecordTableId + " tbody";
    $(tableId).empty();
    let shipsAfloat = 5;
    let playerTag;
    if (gameRecordTableId == "gameRecordOppTable") {
        playerTag = "#opp";
    }
    if (gameRecordTableId == "gameRecordSelfTable") {
        playerTag = "#";
    }

    hitsArray.forEach(function (playTurn) {
        let hitsReport = "";
        if (playTurn.damages.carrierHits > 0){
            hitsReport += "Carrier " + addDamagesIcons(playTurn.damages.carrierHits, "hit") + " ";
            if (playTurn.damages.carrier === 5){
                hitsReport += "SUNK! ";
                $(playerTag + 'carrierIcon').html('<img src="img/carriersunk.png">');
                shipsAfloat--;
            }
        }

        if (playTurn.damages.battleshipHits > 0){
            hitsReport += "Battleship " + addDamagesIcons(playTurn.damages.battleshipHits, "hit") + " ";
            if (playTurn.damages.battleship === 4){
                hitsReport += "SUNK! ";
                $(playerTag + 'battleshipIcon').html('<img src="img/battleshipsunk.png">');
                shipsAfloat--;
            }
        }
        if (playTurn.damages.submarineHits > 0){
            hitsReport += "Submarine " + addDamagesIcons(playTurn.damages.submarineHits, "hit") + " ";
            if (playTurn.damages.submarine === 3){
                hitsReport += "SUNK! ";
                $(playerTag + 'submarineIcon').html('<img src="img/submarinesunk.png">');
                shipsAfloat--;
            }
        }
        if (playTurn.damages.destroyerHits > 0){
            hitsReport += "Destroyer " + addDamagesIcons(playTurn.damages.destroyerHits, "hit") + " ";
            if (playTurn.damages.destroyer === 3){
                hitsReport += "SUNK! ";
                $(playerTag + 'destoyerIcon').html('<img src="img/destoyersunk.png">');
                shipsAfloat--;
            }
        }
        if (playTurn.damages.patrolboatHits > 0){
            hitsReport += "Patrol Boat " + addDamagesIcons(playTurn.damages.patrolboatHits, "hit") + " ";
            if (playTurn.damages.patrolboat === 2){
                hitsReport += "SUNK! ";
                $(playerTag + 'patrolboatIcon').html('<img src="img/patrolboatsunk.png">');
                shipsAfloat--;
            }
        }

        if (playTurn.missed > 0){
            hitsReport +=  "Missed shots " + addDamagesIcons(playTurn.missed, "missed") + " ";
        }

        if (hitsReport === ""){
            hitsReport = "All salvoes missed! No damages!"
        }

        $('<tr><td class="textCenter">' + playTurn.turn + '</td><td>' + hitsReport + '</td></tr>').prependTo(tableId);

    });
    $('#shipsLeftSelfCount').text(shipsAfloat);
}

function addDamagesIcons (numberOfHits, hitOrMissed) {
    let damagesIcons = "";
    if (hitOrMissed === "missed") {
        for (var i = 0; i < numberOfHits; i++) {
            damagesIcons += "<img class='hitblast' src='img/missed.png'>"
        }
    }
        if (hitOrMissed === "hit") {
            for (var i = 0; i < numberOfHits; i++) {
                damagesIcons += "<img class='hitblast' src='img/redhit.png'>"
            }
    }
    return damagesIcons;
}

function getTurn(gamePlayerData) {
    let turn;
    if (gamePlayerData.hits.self.length < gamePlayerData.hits.opponent.length) {
        turn = gamePlayerData.hits.opponent.length;
    } else {
        turn = gamePlayerData.hits.opponent.length + 1;
    }
    return turn;
}





