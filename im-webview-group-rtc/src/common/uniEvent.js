
/**
 * 接收来自uniapp的消息
 */
class UniEvent{}

UniEvent.prototype.listen = function(callback){
	// APP
	window.onEvent = (e)=>{
		let event = JSON.parse(decodeURIComponent(e));
		callback(event.key,event.data)
	}
	// H5
	window.addEventListener('message', function (e) {
		const event = e.data;
		callback(event.key,event.data)
	},false)
	
}

export default UniEvent;