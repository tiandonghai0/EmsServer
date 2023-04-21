sample
===

    select #use("cols")# from t_water_meter_task  where  #use("condition")#

cols
===
	f_id,f_water_meter_number,f_recharge_amount,f_valid_time,f_status,f_create_time,f_tpdate_time,f_create_user,f_update_user,f_is_reset,f_process_status,f_type

updateSample
===

	f_id=#fId#,f_water_meter_number=#fWaterMeterNumber#,f_recharge_amount=#fRechargeAmount#,f_valid_time=#fValidTime#,f_status=#fStatus#,f_create_time=#fCreateTime#,f_tpdate_time=#fTpdateTime#,f_create_user=#fCreateUser#,f_update_user=#fUpdateUser#,f_is_reset=#fIsReset#,f_process_status=#fProcessStatus#,f_type=#fType#

condition
===

    1 = 1
    @if(!isEmpty(fId)){
     and f_id=#fId#
    @}
        @if(!isEmpty(fWaterMeterNumber)){
     and f_water_meter_number=#fWaterMeterNumber#
    @}
        @if(!isEmpty(fRechargeAmount)){
     and f_recharge_amount=#fRechargeAmount#
    @}
        @if(!isEmpty(fValidTime)){
     and f_valid_time=#fValidTime#
    @}
        @if(!isEmpty(fStatus)){
     and f_status=#fStatus#
    @}
        @if(!isEmpty(fCreateTime)){
     and f_create_time=#fCreateTime#
    @}
        @if(!isEmpty(fTpdateTime)){
     and f_tpdate_time=#fTpdateTime#
    @}
        @if(!isEmpty(fCreateUser)){
     and f_create_user=#fCreateUser#
    @}
        @if(!isEmpty(fUpdateUser)){
     and f_update_user=#fUpdateUser#
    @}
        @if(!isEmpty(fIsReset)){
     and f_is_reset=#fIsReset#
    @}
        @if(!isEmpty(fProcessStatus)){
     and f_process_status=#fProcessStatus#
    @}
        @if(!isEmpty(fType)){
     and f_type=#fType#
    @}
    