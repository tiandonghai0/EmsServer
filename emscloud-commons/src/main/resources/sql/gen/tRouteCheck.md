sample
===
* 注释

	select #use("cols")# from t_route_check  where  #use("condition")#

cols
===
	id,device_state,details,project_id,checkman,device_no,create_date,update_date,create_by,update_by

updateSample
===
	
	id=#id#,device_state=#deviceState#,details=#details#,project_id=#projectId#,checkman=#checkman#,device_no=#deviceNo#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(id)){
	 and id=#id#
	@}
	@if(!isEmpty(deviceState)){
	 and device_state=#deviceState#
	@}
	@if(!isEmpty(details)){
	 and details=#details#
	@}
	@if(!isEmpty(projectId)){
	 and project_id=#projectId#
	@}
	@if(!isEmpty(checkman)){
	 and checkman=#checkman#
	@}
	@if(!isEmpty(deviceNo)){
	 and device_no=#deviceNo#
	@}
	@if(!isEmpty(createDate)){
	 and create_date=#createDate#
	@}
	@if(!isEmpty(updateDate)){
	 and update_date=#updateDate#
	@}
	@if(!isEmpty(createBy)){
	 and create_by=#createBy#
	@}
	@if(!isEmpty(updateBy)){
	 and update_by=#updateBy#
	@}
	
	