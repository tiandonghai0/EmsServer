function UpdateFirmware (dgUpdateFirmware) {
	this.UpdateFirmwareDG = new UpdateFirmwareInner(dgUpdateFirmware.dg);
	var _this = this;
	if(typeof UpdateFirmware.initialized == "undefined"){
		UpdateFirmware.prototype.dsearch = function () {
			dgUpdateFirmware.dg.datagrid('options').url = dgUpdateFirmware.datagridurl;
			dgUpdateFirmware.dg.datagrid('load', {});
		};
		UpdateFirmware.prototype.init = function () {
			_this.UpdateFirmwareDG.init();
		};
		UpdateFirmware.initialized = true;
	}
}

function UpdateFirmwareInner (dg) {
	var editIndex = undefined;
	var _hasInit = false;
	var _hasFinished = true;
	var _this = this;

	this.init = function () {
		if (_hasInit) {
			return;
		}
		var pager = dg.datagrid().datagrid('getPager');	// get the pager of datagrid
		pager.pagination({
			displayMsg:'当前显示从第{from}条到{to}条 共{total}条记录',
			showRefresh: true,
		});
		_hasInit = true;
	};
	this.pagerFilter = function (data) {
		if (typeof data.length == 'number' && typeof data.splice == 'function'){	// is array
			data = {
				total: data.length,
				rows: data
			};
		}
		var opts = dg.datagrid('options');
		var pager = dg.datagrid('getPager');
		pager.pagination({
			onSelectPage:function(pageNum, pageSize){
				opts.pageNumber = pageNum;
				opts.pageSize = pageSize;
				pager.pagination('refresh',{
					pageNumber:pageNum,
					pageSize:pageSize
				});
				dg.datagrid('loadData',data);
			}
		});
		if (!data.originalRows){
			data.originalRows = (data.rows);
		}
		var start = (opts.pageNumber-1)*parseInt(opts.pageSize);
		var end = start + parseInt(opts.pageSize);
		data.rows = (data.originalRows.slice(start, end));
		return data;
	};
}