sample
===
    select #use("cols")# from t_water_meter_record  where  #use("condition")#

cols
===
	f_id,f_water_meter_number,f_status,f_tra_id,f_create_time,f_update_time,f_create_user,f_update_user,f_mater_meter_task_id,f_type

updateSample
===

	f_id=#fId#,f_water_meter_number=#fWaterMeterNumber#,f_status=#fStatus#,f_tra_id=#fTraId#,f_create_time=#fCreateTime#,f_update_time=#fUpdateTime#,f_create_user=#fCreateUser#,f_update_user=#fUpdateUser#,f_mater_meter_task_id=#fMaterMeterTaskId#,f_type=#fType#

condition
===

    1 = 1
    @if(!isEmpty(fId)){
     and f_id=#fId#
    @}
        @if(!isEmpty(fWaterMeterNumber)){
     and f_water_meter_number=#fWaterMeterNumber#
    @}
        @if(!isEmpty(fStatus)){
     and f_status=#fStatus#
    @}
        @if(!isEmpty(fTraId)){
     and f_tra_id=#fTraId#
    @}
        @if(!isEmpty(fCreateTime)){
     and f_create_time=#fCreateTime#
    @}
        @if(!isEmpty(fUpdateTime)){
     and f_update_time=#fUpdateTime#
    @}
        @if(!isEmpty(fCreateUser)){
     and f_create_user=#fCreateUser#
    @}
        @if(!isEmpty(fUpdateUser)){
     and f_update_user=#fUpdateUser#
    @}
        @if(!isEmpty(fMaterMeterTaskId)){
     and f_mater_meter_task_id=#fMaterMeterTaskId#
    @}
        @if(!isEmpty(fType)){
     and f_type=#fType#
    @}
    