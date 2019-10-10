var salvo1cellID = "salvoout1";
var salvo2cellID = "salvoout2";
var salvo3cellID = "salvoout3";
var salvo4cellID = "salvoout4";
var salvo5cellID = "salvoout5";

(function() {

    function init() {
        var startPos = null;

        interact('.draggable').draggable({
            snap: {
                targets: [startPos],
                range: Infinity,
                relativePoints: [ { x: 0.5, y: 0.5 } ],
                endOnly: true
            },
            onstart: function (event) {


                var rect = interact.getElementRect(event.target);

                // record center point when starting the very first a drag
                startPos = {
                    x: rect.left + rect.width  / 2,
                    y: rect.top  + rect.height / 2
                };

                event.interactable.draggable({
                    snap: {
                        targets: [startPos]
                    }
                });
            },
            // call this function on every dragmove event
            onmove: function (event) {

                var target = event.target,
                    // keep the dragged position in the data-x/data-y attributes
                    x = (parseFloat(target.getAttribute('data-x')) || 0) + event.dx,
                    y = (parseFloat(target.getAttribute('data-y')) || 0) + event.dy;

                // translate the element
                target.style.webkitTransform =
                    target.style.transform =
                        'translate(' + x + 'px, ' + y + 'px)';

                // update the posiion attributes
                target.setAttribute('data-x', x);
                target.setAttribute('data-y', y);
                target.classList.add('getting--dragged');
            },
            onend: function (event) {
                event.target.classList.remove('getting--dragged')
            }
        });

       interact('.droppable:not(.salvoCell)').dropzone({
            accept: '.draggable',
            overlap: .5,
           checker: function (dragEvent,         // related dragmove or dragend
                              event,             // Touch, Pointer or Mouse Event
                              dropped,           // bool default checker result
                              dropzone,          // dropzone Interactable
                              dropElement,       // dropzone elemnt
                              draggable,         // draggable Interactable
                              draggableElement) {// draggable element


// only allow drops into empty dropzone elements
               return dropped && !dropElement.classList.contains('caught--it');
           },


            ondropactivate: function (event) {



            },
            ondragenter: function (event) {


                var draggableElement = event.relatedTarget,
                    dropzoneElement  = event.target,
                    dropRect         = interact.getElementRect(dropzoneElement),
                    dropCenter       = {
                        x: dropRect.left + dropRect.width  / 2,
                        y: dropRect.top  + dropRect.height / 2
                    };

                event.draggable.draggable({
                    snap: {
                        targets: [dropCenter]
                    }
                });


                // feedback the possibility of a drop
                dropzoneElement.classList.add('can--catch');


            },


            ondragleave: function (event) {
                // remove the drop feedback style
                event.target.classList.remove('can--catch');


            },
            ondrop: function (event) {

                // console.log("Index of dropped node: " + getNodeIndex(event.target));
                // console.log("Index of dragged node: " + getNodeIndex(event.relatedTarget.parentNode));
                // //event.relatedTarget.textContent = 'Dropped';
                // console.log("Dropped!");
                // console.log("related target: " + event.relatedTarget.parentNode);
                console.log(event.relatedTarget.id + " dropped on cell: " + event.target.id);
                switch (event.relatedTarget.id)
                {
                    case "salvo1":
                        salvo1cellID = event.target.id;
                        break;
                    case "salvo2":
                        salvo2cellID = event.target.id;
                        break;
                    case "salvo3":
                        salvo3cellID = event.target.id;
                        break;
                    case "salvo4":
                        salvo4cellID = event.target.id;
                        break;
                    case "salvo5":
                        salvo5cellID = event.target.id;
                        break;
                    default:
                        alert('error');
                }



            },

           ondropmove: function (event) {


           },
            ondropdeactivate: function (event) {
                // remove active dropzone feedback
                event.target.classList.remove('can--catch');
                event.target.classList.remove('caught--it');
                if (event.target.id == salvo1cellID ||
                    event.target.id == salvo2cellID ||
                    event.target.id == salvo3cellID ||
                    event.target.id == salvo4cellID ||
                    event.target.id == salvo5cellID
                ){
                    event.target.classList.add('caught--it');
                }
            }
        });
    }

    function getNodeIndex(node) {
        var index = 0;
        while ( (node = node.previousSibling) ) {
            if (node.nodeType != 3 || !/^\s*$/.test(node.data)) {
                index++;
            }
        }
        return index;
    }

    function eleHasClass(el, cls) {
        return el.className && new RegExp("(\\s|^)" + cls + "(\\s|$)").test(el.className);
    }

    window.onload = function() {
        init();
    }

})();

function resetSalvoCellIds(){
    salvo1cellID = "salvoout1";
    salvo2cellID = "salvoout2";
    salvo3cellID = "salvoout3";
    salvo4cellID = "salvoout4";
    salvo5cellID = "salvoout5";
}



