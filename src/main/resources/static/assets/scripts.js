window.$(function() {

	window.$('.navbar .name-popover').popover({
		html : true,
		content : function() {
			return window.$('#eve_navbar_popover').html();
		}
	});

    CorpCourierContracts.ready();

    window.$('.moon-extraction .table').DataTable({
		"aaSorting" : [], // no initial sorting
		"paging" : false,
		"info" : false,
		"searching" : false,
		fixedHeader : true
	});

    // keep alive ping
    window.setInterval(function () {
        window.$.get('/ping');
    }, 300000); // 5 minutes
});

var Assets = (function($, undefined) { /* jshint ignore: line*/

    return {

        toggleContainer: function(id) {
            $('.location-'+id).toggle();

            if (! $('.location-'+id).is(':visible')) {
                $('.parent-location-'+id).hide();
            }
        },

        toggleAll: function(evt, selector) {
            $.Event(evt).preventDefault();
            if ($(selector).first().is(':visible')) {
                $(selector).hide();
            } else {
                $(selector).show();
            }
        }
    };
})(window.$);


var CorpCourierContracts = (function($, undefined) {

    function calcSums(table) {
        var volume = 0;
        var collateral = 0;
        var reward = 0;
        var calcReward = 0;

        var tc = '.' + table;

        $(tc + ' .selected .volume').each(function() {
            volume += parseFloat($(this).data('value'));
        });
        $(tc + ' .selected .collateral').each(function() {
            collateral += parseFloat($(this).data('value'));
        });
        $(tc + ' .selected .reward').each(function() {
            reward += parseFloat($(this).data('value'));
        });
        $(tc + ' .selected .calc-reward').each(function() {
        	calcReward += parseFloat($(this).data('value'));
        });

        $(tc + ' .sum .volume').text(
    		volume.toLocaleString('en-GB', { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
        $(tc + ' .sum .collateral').text(Math.round(collateral).toLocaleString('en-GB'));
        $(tc + ' .sum .reward').text(Math.round(reward).toLocaleString('en-GB'));
        $(tc + ' .sum .calc-reward').text(Math.round(calcReward).toLocaleString('en-GB'));
    }

    function calcReward() {

    	function getSystem(location) {
    	    for (var i = 0; i < Route.graph.length; i++) {
    	        if (location.indexOf(Route.graph[i].name) === 0) {
    	            return Route.graph[i].name;
    	        }
    	    }
    	}

        function calculateRoute(startLoc, endLoc) {
            if (endLoc === 'Amarr') {
                endLoc = 'Ami';
            } else if (endLoc === 'Mendori') {
                endLoc = 'Rahadalon';
            }
            var startSys = Route.findSystem(startLoc);
            var endSys = Route.findSystem(endLoc);
            return Route.calculateRoute(startSys, endSys, 10);
        }

    	var bltMaxVolume    = 320000;
    	var bltMaxCollat    = 3000000000;
    	var baseFee         = 0;
    	var cynoFee         = 5000000; // per jump
    	var distanceFee     = 8000000; // per ly

    	$('tbody .reward').each(function() {
    		var $start = $(this).closest('tr').find('.start');
    		var $end = $(this).closest('tr').find('.end');
    		var $volumeCell = $(this).closest('tr').find('.volume');
    		var $collatCell = $(this).closest('tr').find('.collateral');
    		var $targetCell = $(this).closest('tr').find('.calc-reward');

            var volume = parseFloat($volumeCell.data('value'));
            var collateral = parseFloat($collatCell.data('value'));

            var startLoc = getSystem($start.text());
            var endLoc = getSystem($end.text());
            var route = calculateRoute(startLoc, endLoc);

            var distance = 0;
            var numJumps = 0;
            var title = '';
            for (var i = 0; i < route.length; i++) {
                distance += route[i].distance;
                numJumps ++;
                if (i === 0) {
                    title += route[i].from.name + ', ';
                }
                title += route[i].to.name + ', ';
            }
            title += distance + ' ly.';

        	var volumeShare     = volume / bltMaxVolume;
        	var collateralShare = collateral / bltMaxCollat;

        	var calcReward = Math.round(baseFee + (
    			(Math.ceil(Math.max(volumeShare, collateralShare, 0.01) * 1000) / 1000) *
    			((distance * distanceFee) + (cynoFee * numJumps))
			));

        	$targetCell.data('value', calcReward);
            $targetCell.text(calcReward.toLocaleString('en-GB'));
            $targetCell.attr('title', title);
        });
    }

    return {

        ready: function() {

            $.getJSON("/vendor/eve-map/systems.json", function(data) {
                Route.graph = data;
                calcReward();
            });

            calcSums('outstanding');
            calcSums('other');

            $('.corp-courier-contracts .table').DataTable({
                "aaSorting" : [], // no initial sorting
                "paging" : false,
                "info" : false,
                "orderCellsTop": true,
                fixedHeader : true,
                initComplete: function() {
                	var cols = this.hasClass('other') ? [0, 1, 2, 8, 9, 10] : [0, 1, 7, 8];
                    this.api().columns(cols).every(function() {
                        var column = this;
                        var select = $('<br><select onclick="$.Event(event).stopPropagation();"></select>')
                            .appendTo($(column.header()))
                            .width('100%')
                            .on('change', function() {
                                column.search($(this).val()).draw();
                            });
                        select.append('<option value=""></option>');
                        column.data().unique().sort().each(function(d) {
                            select.append('<option value="' + d + '">' + d + '</option>');
                        });
                    });
                }
            });
        },

        select: function(ele) {
        	var table;
            if ($(ele).closest('table').hasClass('outstanding')) {
            	table = 'outstanding';
            } else {
            	table = 'other';
            }

            if ($(ele).hasClass('selected')) {
                $(ele).removeClass('selected');
            } else {
                $(ele).addClass('selected');
            }

            calcSums(table);
        }
    };
})(window.$);
