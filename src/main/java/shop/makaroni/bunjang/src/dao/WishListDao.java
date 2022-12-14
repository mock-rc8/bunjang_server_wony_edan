package shop.makaroni.bunjang.src.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class WishListDao {

	private final NamedParameterJdbcTemplate template;

	public Integer countMyWishList(Long userId) {
		var sql = "select count(*) from WishList wl " +
				"where wl.userIdx=:userId " +
				"and status='Y'";
		return template.queryForObject(sql, Map.of("userId", userId), Integer.class);
	}
}
