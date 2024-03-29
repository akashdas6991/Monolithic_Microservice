package com.restaurant.app.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.restaurant.app.entity.JwtDetails;
import com.restaurant.app.repository.JwtRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtService
{
	@Value("${jwt.auth.app}")
	private String appName;
	
	@Value("${jwt.auth.secret_key}")
	private String secretKey;
	
	@Value("${jwt.auth.expires_in}")
	private int expiresIn;

	@Autowired
	private JwtRepository jwtRepository;
	
	private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
	
	private Claims getAllClaimsFromToken(String token) throws Exception {
		Claims claims;
		
		try 
		{
			claims = Jwts.parser()
						 .setSigningKey(secretKey)
						 .parseClaimsJws(token)
						 .getBody();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			claims = null;
			throw new Exception(e.getMessage());
		}
		
		return claims;
	}
	
	public String getUsernameFromToken(String token)
	{
		String username;
		try
		{
			final Claims claims = this.getAllClaimsFromToken(token);
			username = claims.getSubject();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			username = null;		
		}
		
		return username;
	}
	
	public String generateToken(String username)
	{
		return Jwts.builder()
				   .setIssuer(appName)
				   .setSubject(username)
				   .setIssuedAt(new Date())
				   .setExpiration(generateExpirationDate())
				   .signWith(SIGNATURE_ALGORITHM , secretKey)
				   .compact();
	}
	
	private Date generateExpirationDate()
	{
		return new Date(new Date().getTime() + expiresIn * 1000);
	}
	
	public Boolean validateToken(String token,UserDetails userDetails)
	{
		final String username = getUsernameFromToken(token);
		
		return ( username != null && username.equals(userDetails.getUsername())  && !isTokenExpired(token) );
	}
	
	public boolean isTokenExpired(String token)
	{
		Date expireDate = this.getExpirationDate(token);

		return expireDate.before(new Date());
	}
	
	
	private Date getExpirationDate(String token)
	{
		Date expireDate;
		
		try
		{
			final Claims claims = this.getAllClaimsFromToken(token);
			expireDate = claims.getExpiration();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			expireDate = null;
		}
		
		return expireDate;
	}
	
	public Date getIssuedAtDateFromToken(String token)
	{
		Date issueAt;
		
		try
		{
			final Claims claims = this.getAllClaimsFromToken(token);
			issueAt = claims.getIssuedAt();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			issueAt = null;
		}
		
		return issueAt;
	}
	
	public String getToken (HttpServletRequest request)
	{
		String authHeader = getAuthHeaderFromHeader(request);
		if(authHeader != null && authHeader.startsWith("Bearer "))
		{
			return authHeader.substring(7);
		}
		
		return null;
	}
	
	public String getAuthHeaderFromHeader(HttpServletRequest request)
	{
		return request.getHeader("Authorization");
	}

	//new
	public ModelMap validateToken(HttpServletRequest request){

		ModelMap response = new ModelMap();
		String token = this.getToken(request);
		String email = this.getUsernameFromToken(token);
		JwtDetails jwtToken = jwtRepository.findByUserEmail(email);
		DecodedJWT decodedJWT = jwtTokenDecode(token);

		if((jwtToken == null)  || ( !jwtToken.getToken().equals(token)  )) {
			response.put("message", "Invalid Token");
			response.put("httpStatus", HttpStatus.UNAUTHORIZED);
		}
		else if(isJwtTokenExpired(decodedJWT))
		{
			response.put("message","Expired Token");
			response.put("httpStatus",HttpStatus.NOT_ACCEPTABLE);
		}
		else
		{
			response.put("message","Valid Token");
			response.put("httpStatus",HttpStatus.OK);
		}

		return response;
	}

	//new
	public DecodedJWT jwtTokenDecode(String jwtToken)
	{
		try {
			DecodedJWT decodedJWT = JWT.decode(jwtToken);
			return decodedJWT;
		} catch (JWTDecodeException e) {
			e.printStackTrace();
		}

        return null;
    }

	//new
	public boolean isJwtTokenExpired(DecodedJWT decodedJWT)
	{
		Date expiresAt = decodedJWT.getExpiresAt();
		return expiresAt.before(new Date());
	}

}
