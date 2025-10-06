<template>
  <div id="app">
    <chat-video></chat-video>
  </div>
</template>

<script>
import ChatVideo from "./view/ChatVideo.vue"

export default {
  name: 'App',
  components:{
	  ChatVideo
  }
}
</script>

<style lang="scss">
	#app{
		position: relative;
		width: 100%;
		height: 100%;
	}
</style>
