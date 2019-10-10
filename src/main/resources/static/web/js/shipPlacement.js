var carrier = "carrier";
var battleship = "battleship";
var submarine = "submarine";
var destroyer = "destroyer";
var patrolboat = "patrolboat";
var grid;
var notMoved = "";

var positions;
var shipsJSON;



$('#save-grid').click(function () {
	grid.saveGrid();
	renderPositions(positions);
	grid.setStatic(true);
	postShipLocations(makePostUrl());
});


$(function () {
	var options = {
		width: 10,
		height: 10,
		verticalMargin: 0,
		// animate: true,
		cellHeight: 45,
		disableResize: true,
		//	resizable: {
		//    handles: 'e, se, s, sw, w'
		//  },
		float: true,
		removeTimeout: 100,
        disableOneColumnMode: true,
		acceptWidgets: '.salvoShot'
	};


	$('#grid1').gridstack(options);



	grid = $('#grid1').data('gridstack');


	// language=HTML
    grid.addWidget($('<div id="carrier"><div id="carrierHandler" class="grid-stack-item-content carrierHor"><button class="rotateButton" onclick="rotate(carrier)"><img class="rotateIcon" src="img/rotate.png"></button></div><div/>'),
		3, 8, 5, 1, false, 1, 5, 1, 5, "carrier");
	grid.addWidget($('<div id="battleship"><div id="battleshipHandler" class="grid-stack-item-content battleshipHor"><button class="rotateButton" onclick="rotate(battleship)"><img class="rotateIcon" src="img/rotate.png"></button></div><div/>'),
		5, 1, 4, 1, false, 1, 4, 1, 4, "battleship");
	grid.addWidget($('<div id="submarine"><div id="submarineHandler" class="grid-stack-item-content submarineHor"><button class="rotateButton" onclick="rotate(submarine)"><img class="rotateIcon" src="img/rotate.png"></button></div><div/>'),
		1, 5, 3, 1, false, 1, 3, 1, 3, "submarine");
	grid.addWidget($('<div id="destroyer"><div id="destroyerHandler" class="grid-stack-item-content destroyerHor destroyerVer"><button class="rotateButton" onclick="rotate(destroyer)"><img class="rotateIcon" src="img/rotate.png"></button></div><div/>'),
		7, 3, 1, 3, false, 1, 3, 1, 3, "destroyer");
	grid.addWidget($('<div id="patrolboat"><div id="patrolboatHandler" class="grid-stack-item-content patrolboatHor patrolboatVer"><button class="rotateButton" onclick="rotate(patrolboat)"><img class="rotateIcon" src="img/rotate.png"></button></div><div/>'),
		1, 1, 1, 2, false, 1, 2, 1, 2, "patrolboat");

	// $('.iii').draggable({
	// 	revert: 'invalid',
	// 	handle: '.grid-stack-item-content',
	// 	scroll: false,
	// 	appendTo: 'body'
	// });

	grid.saveGrid = function () {
		this.serializedData = _.map($('.grid-stack > .grid-stack-item:visible'), function (el) {
			el = $(el);
			var node = el.data('_gridstack_node');
			return {
				id: node.id,
				x: node.x,
				y: node.y,
				width: node.width,
				height: node.height
			};
		}, this);
		positions = this.serializedData;
		return false;
	}.bind(this);
});

function rotate(ship) {
	shipID = "#" + ship;
	currentWidth = Number($(shipID).attr('data-gs-width'));
	currentHeight = Number($(shipID).attr('data-gs-height'));
	currentX = Number($(shipID).attr('data-gs-x'));
	currentY = Number($(shipID).attr('data-gs-y'));
	verticalClass = ship + "Ver";
	if ((currentHeight == 1) && (grid.isAreaEmpty(currentX, currentY + 1, 1, currentWidth - 1)) && ((currentY + (currentWidth - 1)) < 10)) {
		grid.update($(shipID), currentX, currentY, currentHeight, currentWidth);
		$(shipID + 'Handler').addClass(verticalClass);
        $('.movingMsgBig').html(ship + "<br>rotated to<br> vertical!");
		console.log("x: " + currentX + " y: " + currentY + " w: " + currentHeight + " h: " + currentWidth);
	} else if ((currentWidth == 1) && (grid.isAreaEmpty(currentX + 1, currentY, currentHeight - 1, 1)) && ((currentX + (currentHeight - 1)) < 10)) {
		grid.update($(shipID), currentX, currentY, currentHeight, currentWidth);
        $(shipID + 'Handler').removeClass(verticalClass);
        $('.movingMsgBig').html(ship + "<br>rotated to<br> horizontal!");
		console.log("x: " + currentX + " y: " + currentY + " w: " + currentHeight + " h: " + currentWidth);
	} else {
        var msg = "Illegal position. Collision or out of board!";
		displayOverlay(msg);
		console.log("Illegal position. Collision or Out of board.");
	}

}

function renderPositions(positions) {

	var shipPosition;
	shipData = [];

	for (var i = 0; i < positions.length; i++) {
		shipObject = {};
		
		shipPosition = [];
		firstRowPosition = String.fromCharCode(65 + (positions[i].y));
		firstColPosition = positions[i].x + 1;
		shipPosition.push(firstRowPosition + firstColPosition);
		var nextRow;
		var nextCol;
		if (positions[i].width == 1) {
			for (var j = 1; j < positions[i].height; j++) {
				nextRow = String.fromCharCode(65 + (positions[i].y) + j);
				nextCol = firstColPosition;
				shipPosition.push(nextRow + nextCol);
			}
		}
		if (positions[i].height == 1) {
			for (var j = 1; j < positions[i].width; j++) {
				nextRow = String.fromCharCode(65 + (positions[i].y));
				nextCol = firstColPosition + j;
				shipPosition.push(nextRow + nextCol);
			}
		}
		shipObject = {
			type : positions[i].id,
			locations : shipPosition
		}
		shipData.push(shipObject);
		
	}
	console.log(shipData);
	shipsJSON = JSON.stringify(shipData);
}



function shipPositionMsg(ship) {

    let shipPosition = [];
    shipPositionMsgRendered = "";
    firstRowPosition = String.fromCharCode(65 + (ship.y));
    firstColPosition = ship.x + 1;
    shipPosition.push(firstRowPosition + firstColPosition);
    var nextRow;
    var nextCol;
    if (ship.width == 1) {
        for (var j = 1; j < ship.height; j++) {
            nextRow = String.fromCharCode(65 + (ship.y) + j);
            nextCol = firstColPosition;
            shipPosition.push(nextRow + nextCol);
        }
    }
    if (ship.height == 1) {
        for (var j = 1; j < ship.width; j++) {
            nextRow = String.fromCharCode(65 + (ship.y));
            nextCol = firstColPosition + j;
            shipPosition.push(nextRow + nextCol);
        }
    }
    for (var i = 0; i < shipPosition.length; i++){
        shipPositionMsgRendered += shipPosition[i] + " ";
    }

    return shipPositionMsgRendered;
}

$('#grid1').on('change', function(event, items) {
    items.forEach(function(ship) {
        var shipLocation = shipPositionMsg(ship);
        $('#' + ship.id + 'Position').text(shipLocation).removeClass('movingShip');

   });
    console.log(items);
});

$('#grid1').on('dragstart', function(event, ui) {
    notMoved = $('#' + event.target.id + 'Position').text();
    $('.movingMsgBig').html("..." + event.target.id + "<br>is moving...");
    $('#' + event.target.id + 'Position').text("waiting new position").addClass('movingShip');

});

$('#grid1').on('dragstop', function(event, ui) {

    $('.movingMsgBig').html(event.target.id + "<br>relocated!");
    $('#' + event.target.id + 'Position').text(notMoved).removeClass('movingShip');



});


