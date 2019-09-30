let playersArray;
let gamesData;

$(function() {
    loadData()
});

function updateViewGames(data) {

  var htmlList = data.games.map(function (game) {
      return  '<li class="list-group-item">' + new Date(game.created).toLocaleString() + ' '
      + game.gamePlayers.map(function(element) { return element.player.email}).join(', ') + '</li>';
  }).join('');
  document.getElementById("game-list").innerHTML = htmlList;
}

function getPlayers(data) {

  let players = [];
  let playersIds = [];

  for (let i = 0; i < data.length; i++) {

      for (let j = 0; j < data[i].gamePlayers.length; j++) {

          if (!playersIds.includes(data[i].gamePlayers[j].player.id)) {
              playersIds.push(data[i].gamePlayers[j].player.id);
              let playerScoreData = {
                  "id": data[i].gamePlayers[j].player.id,
                  "email": data[i].gamePlayers[j].player.email,
                  "scores": [],
                  "total": 0.0
              };
              players.push(playerScoreData);
          }
      }
  }
  return players;
}

function addScoresToPlayersArray(players, data) {

  for (let i = 0; i < data.length; i++) {

        for (let j = 0; j < data[i].scores.length; j++) {

                  if(data[i].scores[j]  != null){
                    let scorePlayerId = data[i].scores[j].player;

                                      for (let k = 0; k < players.length; k++) {

                                          if (players[k].id == scorePlayerId) {
                                              players[k].scores.push(data[i].scores[j].score);
                                              players[k].total += data[i].scores[j].score;
                                          }
                                      }
                  }

              }


  }

  return players;
}

function showScoreBoard(players) {

  players.sort(function (a, b) {
      return b.total - a.total;
  });

  let table = "#leader-list";
  $(table).empty();

  for (let m = 0; m < players.length; m++) {
      let countWon = 0;
      let countLost = 0;
      let countTied = 0;

      if (players[m].scores.length > 0) {

          for (let n = 0; n < players[m].scores.length; n++) {
              if (players[m].scores[n] == 0.0) {
                  countLost++;
              } else if (players[m].scores[n] == 0.5) {
                  countTied++;
              } else if (players[m].scores[n] == 1.0) {
                  countWon++;
              }
          }

          let row = $('<tr></tr>').appendTo(table);
          $('<td>' + players[m].email + '</td>').appendTo(row);
          $("<td class='textCenter'>" + players[m].total.toFixed(1) + '</td>').appendTo(row);
          $("<td class='textCenter'>" + countWon + '</td>').appendTo(row);
          $("<td class='textCenter'>" + countLost + '</td>').appendTo(row);
          $("<td class='textCenter'>" + countTied + '</td>').appendTo(row);
      }
  }
}

function loadData() {
  $.get("/api/games")
    .done(function(data) {
      updateViewGames(data);
      gamesData = data.games;
      playersArray  =   getPlayers(gamesData);
      playersArray  =   addScoresToPlayersArray(playersArray,gamesData);
      showScoreBoard(playersArray);
    })
    .fail(function( jqXHR, textStatus ) {
      alert( "Failed: " + textStatus );
    });
}