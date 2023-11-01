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

window.Assets = (function($) {

    // noinspection JSUnusedGlobalSymbols
    return {
        toggleContainer: function(id) {
            const $location = $('.location-'+id);
            $location.toggle();

            if (!$location.is(':visible')) {
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

window.CorpCourierContracts = (function($) {

    function calcSums(table) {
        let volume = 0;
        let collateral = 0;
        let reward = 0;
        let calcReward = 0;

        const column = '.' + table;

        $(column + ' .selected .volume').each(function() {
            volume += parseFloat($(this).data('value'));
        });
        $(column + ' .selected .collateral').each(function() {
            collateral += parseFloat($(this).data('value'));
        });
        $(column + ' .selected .reward').each(function() {
            reward += parseFloat($(this).data('value'));
        });
        $(column + ' .selected .calc-reward').each(function() {
        	calcReward += parseFloat($(this).data('value'));
        });

        $(column + ' .sum .volume').text(volume.toLocaleString(
            'en-GB',
            { minimumFractionDigits: 2, maximumFractionDigits: 2 }
        ));
        $(column + ' .sum .collateral').text(Math.round(collateral).toLocaleString('en-GB'));
        $(column + ' .sum .reward').text(Math.round(reward).toLocaleString('en-GB'));
        $(column + ' .sum .calc-reward').text(Math.round(calcReward).toLocaleString('en-GB'));
    }

    function calcReward() {

    	function getSystem(location) {
    	    for (let i = 0; i < Route.graph.length; i++) {
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
            } else if (endLoc === 'Badivefi') {
                endLoc = 'Efa';
            } else if (endLoc === 'Jita') {
                endLoc = 'Ignoitton';
            } else if (endLoc === 'Purjola') {
                endLoc = 'Maila';
            } else if (endLoc === 'Nonni') {
                endLoc = 'Aunenen';
            } else if (endLoc === 'Otalieto') {
                endLoc = 'Daras';
            } else if (endLoc === 'Onnamon') {
                endLoc = 'Kinakka';
            } else if (endLoc === 'Torrinos') {
                endLoc = 'EC-P8R';
            } else if (endLoc === 'Adacyne') {
                endLoc = 'Maut';
            }
            const startSys = Route.findSystem(startLoc);
            const endSys = Route.findSystem(endLoc);
            return Route.calculateRoute(startSys, endSys, 10);
        }

        const bltMaxVolume = 320000;
        const bltMaxCollateral = 6000000000;
        const baseFee = 0;
        const cynoFee = 0; // per jump
        //const cynoFee = 5000000; // per jump
        //const cynoFee = 15000000; // per non-hub endpoint
        const distanceFee = 10000000; // per ly

    	$('tbody .reward').each(function() {
            const $start = $(this).closest('tr').find('.start');
            const $end = $(this).closest('tr').find('.end');
            const $volumeCell = $(this).closest('tr').find('.volume');
            const $collateralCell = $(this).closest('tr').find('.collateral');
            const $targetCell = $(this).closest('tr').find('.calc-reward');

            const volume = parseFloat($volumeCell.data('value'));
            const collateral = parseFloat($collateralCell.data('value'));

            const startLoc = getSystem($start.text());
            const endLoc = getSystem($end.text());
            const route = calculateRoute(startLoc, endLoc);

            let distance = 0;
            let numJumps = 0;
            let title = '';
            for (let i = 0; i < route.length; i++) {
                distance += route[i].distance;
                numJumps ++;
                if (i === 0) {
                    title += route[i].from.name + ', ';
                }
                title += route[i].to.name + ', ';
            }
            title += (Math.round(distance * 100) / 100) + ' ly.';

            const volumeShare     = volume / bltMaxVolume;
            const collateralShare = collateral / bltMaxCollateral;

            const calcReward = Math.round(baseFee + (
    			(Math.ceil(Math.max(volumeShare, collateralShare, 0.01) * 1000) / 1000) *
    			((distance * distanceFee) + (cynoFee * numJumps))
			));

        	$targetCell.data('value', calcReward);
            $targetCell.text(calcReward.toLocaleString('en-GB'));
            $targetCell.attr('title', title);
        });
    }

    function calcRewardM3() {
        $('.outstanding tbody tr').each(function() {
            const $row = $(this);
            const volume = parseFloat($row.find('.volume').data('value'));
            const reward = parseFloat($row.find('.reward').data('value'));
            $row.find('.reward-m3').html(Math.round(reward / volume).toLocaleString('en-GB'));
        });
    }

    // noinspection JSUnusedGlobalSymbols
    return {

        ready: function() {
            $.getJSON("/vendor/eve-map/systems.json", function(data) {
                Route.graph = data;
                calcReward();
            });

            calcSums('outstanding');
            calcSums('other');
            calcRewardM3();

            // noinspection JSUnusedGlobalSymbols
            $('.corp-courier-contracts .table').DataTable({
                "aaSorting" : [], // no initial sorting
                "paging" : false,
                "info" : false,
                "orderCellsTop": true,
                fixedHeader : true,
                columnDefs: [{
                    targets: 'no-sort',
                    orderable: false,
                }],
                initComplete: function() {
                    const cols = this.hasClass('other') ? [0, 1, 2, 8, 9, 10] : [0, 1, 8, 9];
                    this.api().columns(cols).every(function() {
                        const column = this;
                        const $header = $(column.header());
                        const $select = $('<br><select></select>')
                            .appendTo($header)
                            .width('100%')
                            .on('change', function() {
                                column.search($(this).val()).draw();
                            });
                        $select.append('<option value=""></option>');
                        column.data().unique().sort().each(function(d) {
                            $select.append('<option value="' + d + '">' + d + '</option>');
                        });
                        $header.find('select').on('click', function (evt) {
                            $.Event(evt).stopPropagation();
                        });
                    });
                }
            });
        },

        select: function(ele) {
        	let table;
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
