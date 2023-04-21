var Utils = {};
Utils.EasyUI = (function(){
    var Return = {
		onDataGridBeginEdit : function (index, row) {
			var eds = $(this).datagrid('getEditors', index);
			$.each(eds, function(i, ed) {
				if (ed.type == 'combobox') {
					try {
						ed.actions.setValue(ed.target,eval('row.' + ed.field));
					} catch (e) {
						ed.actions.setValue(ed.target, -1);
					}
				} else if (ed.field.split('.').length > 1) {
					var tempobj = eval('row.' + ed.field.split('.')[0])
					if (tempobj) {
						ed.actions.setValue(ed.target,eval('row.' + ed.field));
					}
				}
			})
		},
		addDefaultCombobox : function (data) {
			var defaultValue = "-1";
			var defaultName = "全部";
			var defaultData = $(this).combobox("options")["defaultComboboxData"];
			var value = $(this).combobox("getValue") ? $(this).combobox("getValue") : "-1";
			if (defaultData) {
				defaultValue = defaultData.id;
				defaultName = defaultData.name;
			};
			var arr = new Array();
			var valueField = $(this).combobox("options")["valueField"];
			var textField = $(this).combobox("options")["textField"];
			var obj = {};
			obj[valueField] = defaultValue;
			obj[textField] = defaultName;
			arr[0] = obj;
			$(this).combobox("select", value);
			return arr.concat(data);
		},
		checkCombox : function (dg, rowIndex, fieldName, message) {
			var combobox = dg.datagrid('getEditor', {index:rowIndex,field:fieldName});
			if ($(combobox.target).combobox('getValue') == -1) {
				$.messager.alert('Warning',message);
				return false;
			};
			return true;
		},
		setDataGridDataForComboNestData : function (dg, editIndex, field, parent, textValue, textName) {
			var ed = dg.datagrid('getEditor', {index:editIndex,field:field});
			if (!ed) {
				return;
			}
			var id = $(ed.target).combobox('getValue');
			var data = $(ed.target).combobox('getData');
			$.each(data, function(index, item) {
				if (item[textValue] == id) {
					if (id == -1) {
						item = undefined;
					}
					var arr = parent.split('.');
					for (var i = 0; i < arr.length; i++) {
						if (!dg.datagrid('getRows')[editIndex][arr[i]]) {
							dg.datagrid('getRows')[editIndex][arr[i]] = {};
						}
					}
					eval("dg.datagrid('getRows')[editIndex]."+parent+"=item");
					return false;
				}
			})
		},
		getComboboxData : function (dg, url, fieldName, isSelectDefaultValue, editIndex) {
			var idx = -1;
			if (!editIndex) {
				var row = dg.datagrid('getSelected');
				var idx = dg.datagrid('getRowIndex', row);
			} else {
				idx = editIndex;
			}
			var name = dg.datagrid('getEditor', {index:idx,field:fieldName})
			$(name.target).combobox('reload', url);
			if (isSelectDefaultValue) {
				$(name.target).combobox('select', -1);
			}
		},
		getFormatDate : function (date) {
	        var seperator1 = "-";
	        var year = date.getFullYear();
	        var month = date.getMonth() + 1;
	        var strDate = date.getDate();
	        if (month >= 1 && month <= 9) {
	            month = "0" + month;
	        }
	        if (strDate >= 0 && strDate <= 9) {
	            strDate = "0" + strDate;
	        }
	        var currentdate = year + seperator1 + month + seperator1 + strDate;
	        return currentdate;
	    },
	    isAmountNumber: function (value) {
	    	return /^\d+(\.\d+)?$/.test(value);
	    }
    };
    return Return;
})();

Utils.Validate = (function(){
    var Return = {
		validate : function (val, options) {
			for(var key in options){
				if(options.hasOwnProperty(key)){
					if (!Utils.Validate.methods[key].call( this, val, options[key])){
						$.messager.alert('警告', Utils.Validate.messages[key]);
						return false;
					};
				}
			}
			return true;
		},
		methods: {
			required: function(value, param) {
				return param && value && value.length > 0;
			},

			email: function(value, param) {
				return param && /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test( value );
			},

			url: function(value, param) {
				return param && /^(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})).?)(?::\d{2,5})?(?:[/?#]\S*)?$/i.test( value );
			},

			date: function(value, param) {
				return param && !/Invalid|NaN/.test( new Date( value ).toString() );
			},
			
			dateISO: function(value, param) {
				return param && /^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])$/.test( value );
			},

			number: function(value, param) {
				return param && /^(?:-?\d+|-?\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test( value );
			},

			digits: function(value, param) {
				return param && /^\d+$/.test( value );
			},
			
			minlength: function( value, param ) {
				var length = value.length;
				return length >= param;
			},
			
			maxlength: function( value, param ) {
				var length = value.length;
				return length <= param;
			},

			rangelength: function( value, param ) {
				var length = value.length;
				return ( length >= param[ 0 ] && length <= param[ 1 ] );
			},

			min: function( value, param ) {
				return value >= param;
			},

			max: function( value, param ) {
				return value <= param;
			},

			range: function( value, param ) {
				return ( value >= param[ 0 ] && value <= param[ 1 ] );
			}
		},
		messages: {
			required: "这是必填字段",
			remote: "请修正此字段",
			email: "请输入有效的电子邮件地址",
			url: "请输入有效的网址",
			date: "请输入有效的日期",
			dateISO: "请输入有效的日期 (YYYY-MM-DD)",
			number: "请输入有效的数字",
			digits: "只能输入数字",
			creditcard: "请输入有效的信用卡号码",
			equalTo: "你的输入不相同",
			extension: "请输入有效的后缀",
			maxlength: "最多可以输入 {0} 个字符",
			minlength: "最少要输入 {0} 个字符",
			rangelength: "请输入长度在 {0} 到 {1} 之间的字符串",
			range: "请输入范围在 {0} 到 {1} 之间的数值",
			max: "请输入不大于 {0} 的数值",
			min: "请输入不小于 {0} 的数值"
		}
    };
    return Return;
})();