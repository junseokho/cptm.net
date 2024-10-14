/**
 * @author 손의현(SONY_STRING)
 * @description 대국 로비와 관련된 컴포넌트들 입니다.
 *
 * todo css 안만들었음
 */

import { useState } from 'react'
import { MatchAPI } from '../apis/MatchAPI.js'


/**
 * @constructor
 * @author 손의현(SONY_STRING)
 * @description 방 목록에서 각 방의 card 입니다. {@link MatchList}에서 사용됩니다.
 * @param {string} match_title 방 제목
 * @param {number} host_rating 방 생성자의 레이팅
 * @param {Array<number>} time_control 대국의 타임 컨트롤 설정
 * @param {number} match_id 대국의 id
 * @return {JSX.Element} 방 Card 컴포넌트
 * @see MatchList
 *
 * @todo user_id 를 최상위 컴포넌트에서 내려받을 좋은 방법 찾는중
 */
function MatchListCard(match_title, host_rating, time_control, match_id) {
    return (
        <>
            <tr className='hook-join' onClick={ () => {
                // todo user_id 는 현재 임시값
                // const joining_room = MatchAPI.join_match(match_id, 1);
                // todo 받은 방 정보로 처리하기 (방 입장 성공/실패 정보를 받아와야함 + 입장 성공하면 실제 게임 화면을 만들어야함)
            }}>
                <td> {match_title} </td><td> {host_rating} </td><td> {time_control} </td>
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
    const [available_matches, set_available_matches] = useState([{title: 'loading...'}]);
    let has_error = false;
    MatchAPI.get_available_matches().then(function(response) {
        set_available_matches(response.data.map((e) => {
            has_error = false;
            // todo 상수로 넣는 값들은 임시값
            return {
                title: e.title,
                host_rating: 1000,
                time_control: '10+5',
                match_id: 1
            }}));
    }).catch(function (error) {
        if (!has_error) {
            has_error = true;
            console.log(error);
            set_available_matches(
                [
                    {title: 'HTTP response met error', host_rating: 0, time_control: '0', match_id: 0},
                ]);
        }
    });
    // todo Warning: Each child in a list should have a unique "key" prop.
    const match_cards = available_matches.map((e) => MatchListCard(e.title, e.host_rating, e.time_control, e.match_id));
    return (
        <>
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
            <div className='lobby'>
                    <MatchList/>
            </div>
        </>
    )
}

export default Lobby;