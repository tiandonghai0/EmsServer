sample
===
* 注释

	select #use("cols")# from t_sub_project  where  #use("condition")#

cols
===
	sub_project_id,project_id,station_id,sub_project_name,create_date,update_date,create_by,update_by

updateSample
===
	
	sub_project_id=#subProjectId#,project_id=#projectId#,station_id=#stationId#,sub_project_name=#subProjectName#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(subProjectId)){
	 and sub_project_id=#subProjectId#
	@}
	@if(!isEmpty(projectId)){
	 and project_id=#projectId#
	@}
	@if(!isEmpty(stationId)){
	 and station_id=#stationId#
	@}
	@if(!isEmpty(subProjectName)){
	 and sub_project_name=#subProjectName#
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
	
	