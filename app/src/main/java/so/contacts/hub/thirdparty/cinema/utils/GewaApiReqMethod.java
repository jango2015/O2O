package so.contacts.hub.thirdparty.cinema.utils;

/**
 * API请求method
 * 
 * @author soul.zhou
 * 
 */
public enum GewaApiReqMethod {
	
	PAYNOTIFY_TEST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.payNotify.test";
		}
	},
	// 购票城市列表
	AREA_CITY_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.cityList";
		}
	},
	// 合作商已开通的城市
	OPEN_PARTNER_CITYLIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.openPartnerCityList";
		}
	},
	// 区县列表
	COUNTY_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.countyList";
		}
	},
	// 商圈列表
	INDEXAREA_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.indexareaList";
		}
	},
	// 影片详情
	MOVIE_DETAIL {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.movieDetail";
		}
	},
	// 即将上映影片列表
	FUTURE_MOVIE_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.futureMovieList";
		}
	},
	// 开放购票影片列表
	OPEN_MOVIE_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.openMovieList";
		}
	},
	// 影院详情
	CINEMA_DETAIL {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.cinemaDetail";
		}
	},
	// 影院影厅列表
	CINEMA_ROOMLIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.roomList";
		}

	},
	// 开放购票的影院列表
	OPEN_CINEMA_LIST_BY_PLAYDATE {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.openCinemaListByPlaydate";
		}
	},
	// 根据城市代码获取该城市下能购票的影院列表
	OPEN_CINEMA_LIST_BY_CITYCODE {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.openCinemaListByCitycode";
		}
	},
	// 影院影厅列表
	ROOM_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.roomList";
		}
	},
	TICKET_HELP {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.ticketHelp";
		}
	},
	// 影厅基础座位信息
	ROOM_SEAT_INFO {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.roomSeatInfo";
		}
	},
	// 购票日期
	PLAYDATE_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.playdateList";
		}
	},
	// 场次详细
	OPI_DETAIL {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.opiDetail";
		}
	},
	// 场次列表
	OPI_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.opiList";
		}
	},
	// 座位信息
	OPI_SEAT_INFO {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.opiSeatInfo";
		}
	},
	// 场次锁定座位信息
	OPI_LOCK_SEAT_INFO {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.opiLockSeatInfo";
		}
	},
	// 创建订单
	TICKETORDER_ADD {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.addTicketOrder";
		}
	},
	// 订单详情
	TICKETORDER_DETAIL {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.ticketOrderDetail";
		}
	},
	TICKETORDER_OTHERINFO {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.ticketOrderOtherInfo";
		}

	},
	TICKETORDER_USECARD {
		@Override
		public String toString() {
			return "com.gewara.partner.order.useCard";
		}
	},
	TICKETORDER_CANCELPAYCARD {
		@Override
		public String toString() {
			return "com.gewara.partner.order.cancelPayCard";
		}
	},
	TICKETORDER_PAYMETHODLIST {
		@Override
		public String toString() {
			return "com.gewara.partner.order.showPayMethodList";
		}
	},
	TICKETORDER_SELECTPAYMETHOD {
		@Override
		public String toString() {
			return "com.gewara.partner.order.selectPayMethod";
		}
	},
	// 订单有效期
	TICKETORDER_VALIDTIME {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.getTicketOrderValidTime";
		}
	},
	// 通知订单支付结果
	PAY_NOTIFY {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.payNotify";
		}
	},
	// 短影评列表
	MOVIE_COMMENT_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.movieCommentList";
		}
	},
	// 长影评列表
	MOVIE_DIARY_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.movieDiaryList";
		}
	},
	// 长影评详细
	MOVIE_DIARY_DETAIL {
		@Override
		public String toString() {
			return "com.gewara.partner.movie.movieDiaryDetail";
		}
	},
	NEWS_DETAIL {
		@Override
		public String toString() {
			return "com.gewara.partner.newsDetail";
		}
	},
	NEWS_LIST {
		@Override
		public String toString() {
			return "com.gewara.partner.newsList";
		}
	},
	CANCEL_OEDER{
		@Override
		public String toString() {
			return "com.gewara.partner.order.cancelOrder";
		}
	};
	public abstract String toString();
}
