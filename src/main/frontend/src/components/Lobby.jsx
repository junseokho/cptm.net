/**
 * @author 손의현(SONY_STRING)
 * @description 대국 로비와 관련된 컴포넌트들 입니다.
 *
 * todo css 미완성
 */

import {useEffect, useState} from 'react'
import { MatchAPI } from '../apis/MatchAPI.js'
import '../assets/css/lobby.scss'


/**
 * @constructor
 * @author 손의현(SONY_STRING)
 * @description 방 목록에서 각 방의 card 입니다. {@link MatchList}에서 사용됩니다.
 * @param {string} matchTitle 방 제목
 * @param {number} hostRating 방 생성자의 레이팅
 * @param {Array<number>} timeControl 대국의 타임 컨트롤 설정
 * @param {number} matchId 대국의 id
 * @return {JSX.Element} 방 Card 컴포넌트
 * @see MatchList
 *
 * @todo user_id 를 최상위 컴포넌트에서 내려받을 좋은 방법 찾는중
 */
function MatchListCard({matchTitle, hostRating, timeControl, matchId}) {
    return (
        <>
            <tr className='hook-join' onClick={ () => {
                // todo user_id 는 현재 임시값
                // const joining_room = MatchAPI.join_match(matchId, 1);
                // todo 받은 방 정보로 처리하기 (방 입장 성공/실패 정보를 받아와야함 + 입장 성공하면 실제 게임 화면을 만들어야함)
            }}>
                <td> {matchTitle} </td><td> {hostRating} </td><td> {timeControl} </td>
            </tr>
        </>
    )
}


/**
 * @constructor
 * @author 손의현(SONY_STRING)
 * @description 방 목록 컴포넌트입니다. {@link Lobby}에서 사용됩니다.
 * @returns {JSX.Element} 방 목록 컴포넌트
 * @see Lobby
 */
function MatchList() {
    const [availableMatches, setAvailableMatches] = useState([{matchTitle: 'loading...', hostRating: 0, timeControl: '0', matchId: 1e9}]);

    const [matchListUpdateCount, setMatchListUpdateCount] = useState(0);
    console.log(matchListUpdateCount);
    useEffect(() => {
        MatchAPI.get_available_matches().then(function(response) {
            const newAvailableMatches = response.data.map((e) => {
                // todo 상수로 넣는 값들은 임시값
                return {
                    matchTitle: e.title,
                    hostRating: 1000,
                    timeControl: '10+5',
                    matchId: e.id
                }});
            setAvailableMatches(newAvailableMatches);
        }).catch(function (error) {
            console.log(error);
            setAvailableMatches(
                [
                    {matchTitle: 'HTTP response met error', hostRating: 0, timeControl: '0', matchId: 0},
                ]);
        });
        setTimeout(() => {setMatchListUpdateCount(s => s+1);}, 1000);
    }, [matchListUpdateCount]);


    const match_cards = availableMatches.map((e) => (
        <MatchListCard matchTitle={e.matchTitle}
                       hostRating={e.hostRating}
                       timeControl={e.timeControl}
                       matchId={e.matchId}
                       key={e.matchId}
        />
    ));
    return (
        <div className='lobby'>
                <table className='hooks-list'>
                    <thead>
                        <tr>
                            <th>title</th><th>rating</th><th>time control</th>
                        </tr>
                    </thead>
                    <tbody>
                        {match_cards}
                    </tbody>
                </table>
        </div>
    )
}


/**
 * @constructor
 * @author 손의현(SONY_STRING)
 * @description 로비의 우측 메뉴 입니다.
 * @returns {JSX.Element}
 */
function MenuRight() {
    // todo Create a Game
    return (
        <>
            <div className="menu">
                <p onClick={
                    () => MatchAPI.create_new_match("host name", "1")
                }>
                    Create a Game
                </p>
            </div>
        </>
    )
}


/**
 * @constructor
 * @author 손의현(SONY_STRING)
 * @description 로비 컴포넌트입니다.
 * @returns {JSX.Element}
 */
function Lobby() {
    return (
        <>
            <div className='body-center'>
                    <MatchList/>
            </div>
            <div className='body-right'>
                <MenuRight/>
            </div>
        </>
    )
}


export default Lobby;