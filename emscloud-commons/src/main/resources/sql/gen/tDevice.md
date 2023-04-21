sample
===
* 注释

	select #use("cols")# from t_device  where  #use("condition")#

cols
===
	device_no,device_id,sub_project_id,device_model,device_name,status,accone_id,create_date,update_date,create_by,update_by

updateSample
===
	
	device_no=#deviceNo#,device_id=#deviceId#,sub_project_id=#subProjectId#,device_model=#deviceModel#,device_name=#deviceName#,status=#status#,accone_id=#acconeId#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(deviceNo)){
	 and device_no=#deviceNo#
	@}
	@if(!isEmpty(deviceId)){
	 and device_id=#deviceId#
	@}
	@if(!isEmpty(subProjectId)){
	 and sub_project_id=#subProjectId#
	@}
	@if(!isEmpty(deviceModel)){
	 and device_model=#deviceModel#
	@}
	@if(!isEmpty(deviceName)){
	 and device_name=#deviceName#
	@}
	@if(!isEmpty(status)){
	 and status=#status#
	@}
	@if(!isEmpty(acconeId)){
	 and accone_id=#acconeId#
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
	
	